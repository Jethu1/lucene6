package FilterToQuery1;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.DocIdSetIterator;

import java.io.IOException;

public class DocPosition {

    private String word;
    private int doc;
    private int freq;
    private int count;
    private int position;
    private PostingsEnum docsEnum;

    public DocPosition(String word, PostingsEnum docsEnum) {
        this.word = word;
        this.docsEnum = docsEnum;
        doc = -1;
    }

    /**
     * 定位到下一个文档
     * @throws IOException
     */
    public boolean nextDoc() throws IOException {
        if (docsEnum == null) {
            return false;
        }
        doc = docsEnum.nextDoc();
        if (doc == DocIdSetIterator.NO_MORE_DOCS) {
            return false;
        }
        freq = docsEnum.freq();
        count = 0;
        position = 0;
        return true;
    }

    /**
     * 定位到下一个位置
     * @throws IOException
     */
    public boolean nextPosition() throws IOException {
        if (count == freq) {
            return false;
        }
        position = docsEnum.nextPosition();
        count++;
        return true;
    }

    /**
     * 跳转到指定的文档
     * @param target     目标文档(即要跳转到的文档ID)
     * @return            有目标文档返回true,否则返回false
     * @throws IOException
     */
    public boolean skipTo(int target) throws IOException {
        if (target < doc) {
            return false;
        }
        if (target > doc) {
            doc = docsEnum.advance(target);
            if (doc == DocIdSetIterator.NO_MORE_DOCS) {
                return false;
            }
        }

        freq = docsEnum.freq();
        count = 0;
        position = 0;

        if (doc != target) {
            return false;
        }
        return true;
    }

    public String getWord() {
        return word;
    }

    public int doc() {
        return doc;
    }

    public int position() {
        return position;
    }
}
