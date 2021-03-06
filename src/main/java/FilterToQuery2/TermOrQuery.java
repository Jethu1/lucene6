package FilterToQuery2;

import FilterToQuery1.NearAfterScorer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 17:30  2017/9/7
 * @Modified By:
 */
public class TermOrQuery extends Query {

    private static Pattern ratioPat = Pattern.compile("(-?\\d+(\\.\\d+)?)%");

    /*
     * 索引域，非数值类型；数值类型的采用PointValues
     */
    private IndexField indexField;

    /*
     * 域值
     */
    private BytesRef[] bytesRefs;

    public TermOrQuery(IndexField indexField, String[] values) {
        this.indexField = indexField;
        bytesRefs = new BytesRef[values.length];
        for (int i = 0; i < values.length; i++) {
            bytesRefs[i] = new BytesRef(values[i]);
        }
    }


    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        return new TermOrQuery.TermOrWeight(searcher, needsScores);
    }

    final class TermOrWeight extends Weight {

        private final boolean needsScores;

        public TermOrWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
            super(TermOrQuery.this);
            this.needsScores = needsScores;
        }

        @Override
        public Scorer scorer(LeafReaderContext context) throws IOException {

            FixedBitSet result = new FixedBitSet(context.reader().maxDoc());
                //寻找匹配的长词--开始：
                Terms terms = context.reader().terms(TermOrQuery.this.indexField.getName());
                if (terms == null) {
                    return null;
                }

                TermsEnum termsEnum = terms.iterator();
                if (termsEnum == null) {
                    return null;
                }

                PostingsEnum docs = null;
                for (BytesRef bytesRef : bytesRefs) {
                    if (termsEnum.seekExact(bytesRef)) {
                        docs = termsEnum.postings(null, PostingsEnum.ALL);
                        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                            result.set(docs.docID());
                        }
                    }
                }
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
