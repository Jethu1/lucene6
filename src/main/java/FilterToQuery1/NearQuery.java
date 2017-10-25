package FilterToQuery1;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 15:02  2017/9/5
 * @Modified By:
 */

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NearQuery extends Query {

    private String field;              //分词字段
    private int slop = 10;             //词间距
    private boolean inOrder;          //是否有前后顺序
    private List<String[]> words = new ArrayList<String[]>();

    public NearQuery(String field,String[] items,int slop,boolean inOrder) {
        for(String item : items) {
            List<String> valueList = segmentText(item);
            words.add(valueList.toArray(new String[0]));
        }
        this.slop = slop;
        this.field = field;
        this.inOrder = inOrder;
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

    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        return new NearQuery.NearWeight(searcher, needsScores);
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


    final class NearWeight extends Weight {

        private final boolean needsScores;

        public NearWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            super(NearQuery.this);
           this.needsScores = needsScores;
        }

        @Override
        public Scorer scorer(LeafReaderContext context) throws IOException {
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
                return new NearScorer(this, idSet.iterator());
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
}
