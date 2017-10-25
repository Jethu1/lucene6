package FilterToQuery1;

import FilterQueryTest.SpanFilterQuery1;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;


/**
 * Created by jet on 2017/7/16.
 */
public class SpanFilterSearcher {

    //这个方法是搜索索引的方法，传入索引路径和查询表达式
    @Test
    public  void search() throws IOException, ParseException {
        String indexDir = "30index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
    /*
     *   SpanNearQuery: documents and searching time.
     */
        // these cache and policy instances can be shared across several queries and readers
        // it is fine to eg. store them into static variables
        final int maxNumberOfCachedQueries = 256;
        final long maxRamBytesUsed = 50 * 1024L * 1024L; // 50MB
        final QueryCache queryCache = new LRUQueryCache(maxNumberOfCachedQueries, maxRamBytesUsed);
        final QueryCachingPolicy defaultCachingPolicy = new UsageTrackingQueryCachingPolicy();
        indexSearcher.setQueryCache(queryCache);
        indexSearcher.setQueryCachingPolicy(defaultCachingPolicy);

        String[] str = new String[3];
        str[0] = "毕业生";
        str[1] = "幸运的";
        str[2] = "不如你";

        String[] str1 = new String[1];
        str1[0] = "毕业生";
//        str[1] = "业";
//        str[2] = "生";
        SpanQuery[] queries = new SpanQuery[3];
        queries[0] = new SpanTermQuery(new Term("contents","毕"));
        queries[1] = new SpanTermQuery(new Term("contents","业"));
        queries[2] = new SpanTermQuery(new Term("contents","生"));
        SpanNearQuery query = new SpanNearQuery(queries,0,true);
        TermQuery termQuery = new TermQuery(new Term("contents","毕"));
        NearAfterQuery nearQuery = new NearAfterQuery("contents",str,10,true);

        SpanFilterQuery1 spanFilterQuery = new SpanFilterQuery1("contents",str,20,true);

        TotalHitCountCollector countCollector = new TotalHitCountCollector();
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(nearQuery, BooleanClause.Occur.FILTER);
        BooleanQuery booleanQuery = builder.build();
        Long begin = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long mid = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long end = System.currentTimeMillis();
        Long begin1 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long mid1 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long end1 = System.currentTimeMillis();
        Long begin2 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long mid2 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long end2 = System.currentTimeMillis();
        Long begin3 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long mid3 = System.currentTimeMillis();
        indexSearcher.search(nearQuery,countCollector);
        Long end3 = System.currentTimeMillis();
//        System.out.println("total documents: "+hits.totalHits+"     scoreDocs: "+hits.scoreDocs[0]);
        System.out.println("SpanNearQuery search time: "+ (mid-begin)+"   The second time to search: "+(end-mid));
        System.out.println("SpanNearQuery search time: "+ (mid1-begin1)+"   The second time to search: "+(end1-mid1));
        System.out.println("SpanNearQuery search time: "+ (mid2-begin2)+"   The second time to search: "+(end2-mid2));
        System.out.println("SpanNearQuery search time: "+ (mid3-begin3)+"   The second time to search: "+(end3-mid3));

    }
}
