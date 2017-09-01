package Lucene6IndexAbility;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
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
public class Searcher {

    //这个方法是搜索索引的方法，传入索引路径和查询表达式
    @Test
    public  void search() throws IOException, ParseException {
        String indexDir = "10index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
/*
    SpanNearQuery: documents and searching time.
  */
        SpanQuery[] queries = new SpanQuery[3];
        queries[0] = new SpanTermQuery(new Term("contents","程"));
        queries[1] = new SpanTermQuery(new Term("contents","序"));
        queries[2] = new SpanTermQuery(new Term("contents","员"));
//        queries[3] = new SpanTermQuery(new Term("contents","像"));
        SpanNearQuery spanNearQuery = new SpanNearQuery(queries,0,true);

        //booleanQuery 中使用filter查询也可以让该部分的查询结果不参与打分
//        builder.add(termQuery, BooleanClause.Occur.MUST);



        String[] str1 = new String[]{"十","年","后"};
        String[] str2 = new String[]{"参","加","了"};
        SpanFilterQuery spanFilterQuery = new SpanFilterQuery("contents",str2,str1,5,false);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(spanFilterQuery.getQuery(), BooleanClause.Occur.FILTER);
        BooleanQuery booleanQuery = builder.build();
        Long begin2 = System.currentTimeMillis();
        TopDocs hits=searcher.search(booleanQuery,3);
        Long mid = System.currentTimeMillis();
        System.out.println("total documents: "+hits.totalHits);

        System.out.println("SpanNearQuery search time: "+ (mid-begin2));


    }



}
