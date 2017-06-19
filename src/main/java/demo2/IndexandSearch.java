package demo2;

/**
 * Created by jet on 2017/6/19.
 */
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


//注意jdk需要1.8 比较坑
public class IndexandSearch {

    private static Document createDocument(String title, String content) {
        Document doc = new Document();
        doc.add(new Field("content", content, TextField.TYPE_NOT_STORED));//不记录在索引中
        doc.add(new Field("title", title, TextField.TYPE_STORED));
        doc.add(new Field("author", "bobliu", TextField.TYPE_STORED));
        return doc;
    }


    /**
     * lucene简单实例 索引 查询 经济,分词器：标准分词器
     */
    public static void testDemo() throws Exception{
        Analyzer analyzer = new StandardAnalyzer();;
        Directory idx = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(idx, iwc);
        writer.addDocument(createDocument("中央政治局研究2017年经济工作", "中共中央政治局12月9日召开会议，分析研究2017年经济工作，审议通过《关于加强国家安全工作的意见》。中共中央总书记习近平主持会议。"));
        writer.addDocument(createDocument("中央政治局研究2017年经济工作new","test中共中央政治局12月9日召开会议，分析研究2017年经济工作，审议通过《关于加强国家安全工作的意见》。中共中央总书记习近平主持会议。1111111"));
        writer.commit();
        writer.close();

        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(idx));
        System.out.println("命中个数:"+searcher.search(new QueryParser("title", analyzer).parse("经济"), 10).totalHits);

        TopDocs topdoc =  searcher.search(new QueryParser("title", analyzer).parse("经济"), 10);
        ScoreDoc[] hits=  topdoc.scoreDocs;// 	scoreDocs The top hits for the query.

        if(hits!=null && hits.length>0){
            for(int i = 0; i < hits.length; i++){
                Document hitDoc = searcher.doc(hits[i].doc);
                System.out.println(hitDoc.get("content"));

                System.out.println(hitDoc.get("author"));
            }
        }


    }
    public static void main(String[] args) {
// TODO Auto-generated method stub
        try {
            testDemo();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
