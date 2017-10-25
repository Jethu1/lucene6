package FilterToQuery2;

import FilterToQuery1.NearAfterScorer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.*;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 17:30  2017/9/7
 * @Modified By:
 */
public class TermOffsetQuery extends Query {

    /**
     * 字段名
     */
    private String field;

    /**
     * 字段值
     */
    private List<String> values;

    /**
     * 起始位置
     */
    private int start;

    /**
     * 结束位置
     */
    private int end;

    public TermOffsetQuery(String field, List<String> values, int start, int end) {
        this.field = field;
        this.values = values;
        this.start = start;
        this.end = end;
    }

    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        return new TermOffsetQuery.TermOffsetWeight(searcher, needsScores);
    }


    final class TermOffsetWeight extends Weight {

        private final boolean needsScores;

        public TermOffsetWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            super(TermOffsetQuery.this);
            this.needsScores = needsScores;
        }

        @Override
        public Scorer scorer(LeafReaderContext context) throws IOException {

            FixedBitSet result = new FixedBitSet(context.reader().maxDoc());

            //寻找匹配的长词--开始：
            Terms terms = context.reader().terms(TermOffsetQuery.this.field);
            if (terms == null) {
                return null;
            }

            TermsEnum termsEnum = terms.iterator();
            if (termsEnum == null) {
                return null;
            }
            //用于存储每一个文档下，每一个term的位置列表
            Map<Integer,Map<Integer, List<Integer>>> docs
                    = new HashMap<Integer,Map<Integer, List<Integer>>>();

            for(int i = 0; i < values.size(); i++) {
                if (termsEnum.seekExact(new BytesRef(values.get(i)))) {
                    PostingsEnum positionsEnum =termsEnum.postings(null, PostingsEnum.ALL);
                    while (positionsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                        //如果无位置信息，则直接略过该文档
                        if(positionsEnum.freq() < 1) {
                            continue;
                        }

                        //获取term的位置列表
                        List<Integer> termPositionList = new ArrayList<Integer>();
                        for (int j = 0; j < positionsEnum.freq(); j++) {
                            int position = positionsEnum.nextPosition();
                            if(position >= start && position <= end) {
                                termPositionList.add(position);
                            }
                        }

                        //无指定范围内的位置信息，直接略过
                        if(termPositionList.size() < 1) {
                            continue;
                        }

                        //将term的位置列表添加到当前文档
                        int docID = positionsEnum.docID();
                        if(docs.containsKey(docID)) {
                            docs.get(docID).put(i,termPositionList);
                        } else {
                            Map<Integer, List<Integer>> positionMap =  new HashMap<Integer, List<Integer>>();
                            positionMap.put(i, termPositionList);
                            docs.put(docID, positionMap);
                        }

                        //计算每个文档下，该词是否在指定的位置范围内出现，如果出现则返回该文档
                        if(i + 1 == values.size()){
                            Map<Integer,List<Integer>> positions = docs.get(docID);
                            //如果所有的term都有位置信息，则判断该文档下的term是否依次相邻，如果相邻词频加1
                            if (positions.size() == values.size()) {
                                Iterator<Integer> it = positions.get(0).iterator();
                                while (it.hasNext()) {
                                    int position = it.next();
                                    if(values.size() == 1) {
                                        result.set(docID);
                                    } else {
                                        boolean find = true;
                                        for (int j = 1; j < values.size(); j++) {
                                            if (!positions.containsKey(j) || !positions.get(j).contains(position + j)) {
                                                find = false;
                                                break;
                                            }
                                        }
                                        if(find){
                                            result.set(docID);
                                        }
                                    }
                                }
                            }

                            positions.clear();
                        }
                    }
                }
            }

            docs.clear();

            BitDocIdSet idSet = new BitDocIdSet(result);
            //寻找匹配的长词--结束：
            if (idSet == null) {
                return null;
            } else {
                return new NearAfterScorer(this, idSet.iterator());
            }
        }

        @Override
        public void extractTerms(Set<Term> set) {

        }

        @Override
        public Explanation explain(LeafReaderContext leafReaderContext, int i) throws IOException {
            return null;
        }

        @Override
        public float getValueForNormalization() throws IOException {
            return 0;
        }

        @Override
        public void normalize(float v, float v1) {   }
    }

    @Override
    public String toString(String s) {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
