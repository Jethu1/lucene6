package collector;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorer;

import java.io.IOException;

/**
 * 用于并行搜索的Collector
 *
 * @author qinxiaoqing
 * @version 1.0, 2016/1/18
 * @see org.apache.lucene.search.Collector
 * @since 1.0
 */
public abstract class ParallelCollector implements LeafCollector,Collector {

    protected LeafReader reader;

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        reader = context.reader();
        return this;
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    @Override
    public void collect(int doc) throws IOException {

    }

    /**
     * 采样数
     */
    protected int sample;

    /**
     * 遍历的文档数
     */
    protected int docCount;

    public void setSample(int sample) {
        this.sample = sample;
    }

    public int getSample() {
        return sample;
    }


    public int getDocCount() {
        return docCount;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        // 不需要打分
    }

    /**
     * 克隆
     */
    public abstract ParallelCollector clone();

    /**
     * 合并
     */
    public abstract void merge(ParallelCollector collector);
}
