package FilterToQuery;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 15:02  2017/9/5
 * @Modified By:
 */

import FilterToQuery1.DocPosition;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.Similarity.SimScorer;
import org.apache.lucene.search.similarities.Similarity.SimWeight;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NearQuery extends Query {
    private final Term term;
    private final TermContext perReaderTermState;
    private String field;              //分词字段
    private int slop = 10;             //词间距
    private boolean inOrder;          //是否有前后顺序
    private List<String[]> words = new ArrayList<String[]>();

    public NearQuery(String field,String[] items,int slop,boolean inOrder,Term t) {
        for(String item : items) {
            List<String> valueList = segmentText(item);
            words.add(valueList.toArray(new String[0]));
        }
        this.term = (Term)Objects.requireNonNull(t);
        this.slop = slop;
        this.field = field;
        this.inOrder = inOrder;
        this.perReaderTermState = null;
    }

    public NearQuery(Term t) {
        this.term = (Term)Objects.requireNonNull(t);
        this.perReaderTermState = null;
    }

    public List<String> segmentText(String item) {
        List<String> list = new ArrayList<String>();
       if(item!=null){
           for (int i = 0; i < item.length(); i++) {
               list.add(item.substring(i,i+1));
           }
       }
        return  list;
    }

    public NearQuery(Term t, TermContext states) {
        assert states != null;

        this.term = (Term)Objects.requireNonNull(t);
        this.perReaderTermState = (TermContext)Objects.requireNonNull(states);
    }

    public Term getTerm() {
        return this.term;
    }

    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        IndexReaderContext context = searcher.getTopReaderContext();
        TermContext termState;
        if(this.perReaderTermState != null && this.perReaderTermState.wasBuiltFor(context)) {
            termState = this.perReaderTermState;
        } else if(needsScores) {
            termState = TermContext.build(context, this.term);
        } else {
            termState = null;
        }

        return new NearQuery.NearWeight(searcher, needsScores, termState);
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if(!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }

        buffer.append(this.term.text());
        return buffer.toString();
    }

    public boolean equals(Object other) {
        return this.sameClassAs(other) && this.term.equals(((NearQuery)other).term);
    }

    public int hashCode() {
        return this.classHash() ^ this.term.hashCode();
    }

    final class NearWeight extends Weight {
        private final Similarity similarity;
        private final SimWeight stats;
        private final TermContext termStates;
        private final boolean needsScores;

        public NearWeight(IndexSearcher searcher, boolean needsScores, TermContext termStates) throws IOException {
            super(NearQuery.this);
            if(needsScores && termStates == null) {
                throw new IllegalStateException("termStates are required when scores are needed");
            } else {
                this.needsScores = needsScores;
                this.termStates = termStates;
                this.similarity = searcher.getSimilarity(needsScores);
                CollectionStatistics collectionStats;
                TermStatistics termStats;
                if(needsScores) {
                    collectionStats = searcher.collectionStatistics(NearQuery.this.term.field());
                    termStats = searcher.termStatistics(NearQuery.this.term, termStates);
                } else {
                    int maxDoc = searcher.getIndexReader().maxDoc();
                    collectionStats = new CollectionStatistics(NearQuery.this.term.field(), (long)maxDoc, -1L, -1L, -1L);
                    termStats = new TermStatistics(NearQuery.this.term.bytes(), (long)maxDoc, -1L);
                }

                this.stats = this.similarity.computeWeight(collectionStats, new TermStatistics[]{termStats});
            }
        }

        public void extractTerms(Set<Term> terms) {
            terms.add(NearQuery.this.getTerm());
        }

        public String toString() {
            return "weight(" + NearQuery.this + ")";
        }

        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }

        public void normalize(float queryNorm, float boost) {
            this.stats.normalize(queryNorm, boost);
        }

        public Scorer scorer(LeafReaderContext context) throws IOException {
            assert this.termStates == null || this.termStates.wasBuiltFor(ReaderUtil.getTopLevelContext(context)) : "The top-reader used to create Weight is not the same as the current reader\'s top-reader (" + ReaderUtil.getTopLevelContext(context);
            FixedBitSet result = new FixedBitSet(context.reader().maxDoc());

            //寻找匹配的长词--开始：
            Terms terms = context.reader().terms(NearQuery.this.field);
            if (terms == null) {
                return null;
            }

            TermsEnum termsEnum = terms.iterator();
            if (termsEnum == null) {
                return null;
            }

            List<DocPosition[]> docPositions = new ArrayList<DocPosition[]>();
            for (String[] word : words) {
                DocPosition[] docPos = new DocPosition[word.length];
                for (int i = 0; i < word.length; i++) {
                    BytesRef bytesRef = new BytesRef(word[i]);
                    if (!termsEnum.seekExact(bytesRef)) {
                        return null;
                    }
                    docPos[i] = new DocPosition(word[i], termsEnum.postings(null,PostingsEnum.POSITIONS));
                }

                docPositions.add(docPos);
            }

            //统计命中文档
            DocPosition[] firstDocPosition = docPositions.get(0);
            while (firstDocPosition[0].nextDoc()) {
                //获取当前docID
                int docID = firstDocPosition[0].doc();

                boolean hit = true;
                for (int i = 1; i < firstDocPosition.length; i++) {
                    //将其后的term跳转到当前文档位置
                    //如果返回false说明文档中没有该term,直接返回
                    if (!firstDocPosition[i].skipTo(docID)) {
                        hit = false;
                        break;
                    }
                }

                if (!hit) {
                    continue;
                }

                for (int i = 1; i < docPositions.size(); i++) {
                    //如果前一个词中的每一个term都有在当前文档中存在，并且后一个词不为空，则将term跳转到当前文档
                    for (int j = 0; hit && j < docPositions.get(i).length; j++) {
                        if (!docPositions.get(i)[j].skipTo(docID)) {
                            hit = false;
                            break;
                        }
                    }
                }

                if (!hit) {
                    continue;
                }

                //计算前、后词的term是否依次相邻，并且两个词之间的距离是否在指定的位置之内
                if (docPositions.size() == 1) {
                    if (SearchUtils.findFollowPosition(docPositions.get(0), 0, Integer.MAX_VALUE)) {
                        result.set(docID);
                    }
                } else if (docPositions.size() == 2) {
                    if (SearchUtils.findFollowPosition(docPositions.get(0), 0, Integer.MAX_VALUE)
                            && SearchUtils.findFollowPosition(docPositions.get(1), 0, Integer.MAX_VALUE)
                            && SearchUtils.findSlopPosition(docPositions.get(0),
                            docPositions.get(1), NearQuery.this.slop, NearQuery.this.inOrder, 0, Integer.MAX_VALUE)) {
                        result.set(docID);
                    }
                } else if (docPositions.size() == 3) {
                    boolean flag = SearchUtils.findMultiAfterPosition(docPositions,
                            NearQuery.this.slop, NearQuery.this.inOrder, 0, Integer.MAX_VALUE);
                    int slop = docPositions.get(2)[0].position() - docPositions.get(0)[0].position()
                            - (docPositions.get(0).length + docPositions.get(1).length);
                    while (flag) {
                        if (slop > NearQuery.this.slop * 2) {
                            flag = SearchUtils.findMultiAfterPosition(docPositions,
                                    NearQuery.this.slop, NearQuery.this.inOrder, 0, Integer.MAX_VALUE);
                        } else {
                            result.set(docID);
                            break;
                        }
                    }
                } else {
                    continue;
                }
            }
            BitDocIdSet idSet = new BitDocIdSet(result);

            //寻找匹配的长词--结束：
            if (idSet == null) {
                return null;
            } else {
                return new NearScorer(this, idSet.iterator(), this.similarity.simScorer(this.stats, context));
            }
        }

        private TermsEnum getTermsEnum(LeafReaderContext context) throws IOException {
            TermsEnum termsEnum;
            if(this.termStates != null) {
                assert this.termStates.wasBuiltFor(ReaderUtil.getTopLevelContext(context)) : "The top-reader used to create Weight is not the same as the current reader\'s top-reader (" + ReaderUtil.getTopLevelContext(context);

                TermState terms1 = this.termStates.get(context.ord);
                if(terms1 == null) {
                    assert this.termNotInReader(context.reader(), NearQuery.this.term) : "no termstate found but term exists in reader term=" + NearQuery.this.term;

                    return null;
                } else {
                    termsEnum = context.reader().terms(NearQuery.this.term.field()).iterator();
                    termsEnum.seekExact(NearQuery.this.term.bytes(), terms1);
                    return termsEnum;
                }
            } else {
                Terms terms = context.reader().terms(NearQuery.this.term.field());
                if(terms == null) {
                    return null;
                } else {
                    termsEnum = terms.iterator();
                    return termsEnum.seekExact(NearQuery.this.term.bytes())?termsEnum:null;
                }
            }
        }

        private boolean termNotInReader(LeafReader reader, Term term) throws IOException {
            return reader.docFreq(term) == 0;
        }

        public Explanation explain(LeafReaderContext context, int doc) throws IOException {
            Scorer scorer = this.scorer(context);
            if(scorer != null) {
                int newDoc = scorer.iterator().advance(doc);
                if(newDoc == doc) {
                    float freq = (float)scorer.freq();
                    SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                    Explanation freqExplanation = Explanation.match(freq, "termFreq=" + freq, new Explanation[0]);
                    Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    return Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", new Explanation[]{scoreExplanation});
                }
            }

            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
    }
}
