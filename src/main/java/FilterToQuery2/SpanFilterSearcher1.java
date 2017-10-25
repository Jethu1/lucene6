package FilterToQuery2;

import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;


/**
 * Created by jet on 2017/7/16.
 */
public class SpanFilterSearcher1 {

    //这个方法是搜索索引的方法，传入索引路径和查询表达式
    @Test
    public  void search() throws IOException, ParseException {
        String indexDir = "30index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));

        String[] str = new String[2];
        str[0] = "IPMC";
        str[1] = "BOCOM_CC";
//        str[2] = "1";
        IndexField indexField = new IndexField();
        indexField.setName("name");
        indexField.setType("String");

        String[] str1 = new String[3];
        str1[0] = "3.5";
        str1[1] = "4.5";
        str1[2] = "5.5";
        float[] floats = new float[3];
        floats[0] = 3.5f;
        floats[1] = 4.5f;
        floats[2] = 5.5f;
//        str1[3] = "6.5";
//        str1[4] = "7.5";
//        str1[5] = "8.5";
        IndexField indexField1 = new IndexField();
        indexField1.setName("IntV");
        indexField1.setType("float");

//        NearAfterQuery nearQuery = new NearAfterQuery("contents",str,10,true);
//        TermFreqQuery freqQuery = new TermFreqQuery("contents",list,1,300);
//        TermOffsetQuery offsetQuery = new TermOffsetQuery("contents",list,0,6000);

        TermOrQuery orQuery = new TermOrQuery(indexField,str);
//        SpanFilterQuery1 spanFilterQuery = new SpanFilterQuery1("contents",str,20,true);
//
//        TotalHitCountCollector countCollector = new TotalHitCountCollector();
//        Long begin = System.currentTimeMillis();
//        TopDocs hits=indexSearcher.search(orQuery,10);
//        Long mid = System.currentTimeMillis();
//
//
//        System.out.println("total documents: "+hits.totalHits);
//        System.out.println("FilterQuery search time: "+ (mid-begin));


        Query query = FloatPoint.newSetQuery("IntV",floats);
        Long begin1 = System.currentTimeMillis();
        TopDocs hits1=indexSearcher.search(query,10);
        Long begin2 = System.currentTimeMillis();
        System.out.println("total documents: "+hits1.totalHits+" time: "+(begin2-begin1));
    }
}
