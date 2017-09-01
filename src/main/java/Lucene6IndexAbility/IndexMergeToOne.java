package Lucene6IndexAbility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by jet on 2017/8/4.
 * 重新构建索引类，重点在于给传进来的item的 list 按时间顺序排序，不然索引的时候要出错。
 */
public class IndexMergeToOne {
    static Logger logger = LogManager.getLogger(IndexMergeToOne.class.getName());
    @Test
    public void index() throws IOException {
        //create a indexWriter"
        Directory directory = FSDirectory.open(Paths.get("50index"));
        Analyzer timeAnalyzer = new StandardAnalyzer();
        IndexWriterConfig writerConfig = new IndexWriterConfig(timeAnalyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//        writerConfig.getMergePolicy();
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);
        Long begin = System.currentTimeMillis();
        indexWriter.forceMerge(1);
        //parser voice xml documents and then indexes  these documents.
        indexWriter.close();
        Long end = System.currentTimeMillis();
        System.out.println("合并索引所需时间为： "+(end-begin));

    }
}
