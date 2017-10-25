package demo1;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
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
public class IndexTest5 {
    static Logger logger = LogManager.getLogger(IndexTest5.class.getName());
    @Test
    public void index() throws IOException {
        //create a indexWriter"
        Directory directory = FSDirectory.open(Paths.get("100voice"));
        IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//        writerConfig.getMergePolicy();
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);
//        indexWriter.forceMerge(5);
        //parser voice xml documents and then indexes  these documents.
         Document[] doc = new Document[1000];
        for (int m = 0; m < 10; m++) {
            for (int i = 0; i < 100; i++) {
                doc[10*i] = new Document();
                doc[10*i+1] = new Document();
                doc[10*i+2] = new Document();
                doc[10*i+3] = new Document();
                doc[10*i+4] = new Document();
                doc[10*i+5] = new Document();
                doc[10*i+6] = new Document();
                doc[10*i+7] = new Document();
                doc[10*i+8] = new Document();
                doc[10*i+9] = new Document();


                doc[10*i].add(new FloatPoint("IntV",2.5f));

                doc[10*i].add(new StringField("name","IPMC", Field.Store.YES));
                doc[10*i].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));


                doc[10*i+1].add(new FloatPoint("IntV",3.5f));

                doc[10*i+1].add(new StringField("name","CITIC", Field.Store.YES));
                doc[10*i+1].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));


                doc[10*i+2].add(new FloatPoint("IntV",4.5f));

                doc[10*i+2].add(new StringField("name","BOCOM_CC", Field.Store.YES));
                doc[10*i+2].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));


                doc[10*i+3].add(new FloatPoint("IntV",5.5f));

                doc[10*i+3].add(new StringField("name","IPMC", Field.Store.YES));
                doc[10*i+3].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));


                doc[10*i+4].add(new FloatPoint("IntV",6.5f));

                doc[10*i+4].add(new StringField("name","S2S", Field.Store.YES));
                doc[10*i+4].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

                doc[10*i+5].add(new FloatPoint("IntV",7.5f));
                doc[10*i+5].add(new StringField("name","IPMC", Field.Store.YES));
                doc[10*i+5].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

                doc[10*i+6].add(new FloatPoint("IntV",8.5f));
                doc[10*i+6].add(new StringField("name","CITIC", Field.Store.YES));
                doc[10*i+6].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

                doc[10*i+7].add(new FloatPoint("IntV",9.5f));
                doc[10*i+7].add(new StringField("name","BOCOM_CC", Field.Store.YES));
                doc[10*i+7].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

                doc[10*i+8].add(new FloatPoint("IntV",10.5f));
                doc[10*i+8].add(new StringField("name","IPMC", Field.Store.YES));
                doc[10*i+8].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

                doc[10*i+9].add(new FloatPoint("IntV",11.5f));
                doc[10*i+9].add(new StringField("name","S7S", Field.Store.YES));
                doc[10*i+9].add(new TextField("contents","喂你  谢谢  再见 好 呃 您 内 个 什么 送过来 让 他 上  天恒 大厦 嗯 行 妈妈 荣 乐 科 的话 就像 说 啊 说 啊 到 了吗 毕 竟在 路 上了 嗯 对 呀 啊 行 那我 在 本地 号", Field.Store.YES));

            }

//            System.out.println("构建1000个随机数组时间： "+(System.currentTimeMillis()-begin1));
            Long begin2 = System.currentTimeMillis();
            for (int k = 0; k < 1000; k++) {
                indexWriter.addDocument(doc[k]);
            }
            indexWriter.commit();

        }

        indexWriter.close();
    }
}
