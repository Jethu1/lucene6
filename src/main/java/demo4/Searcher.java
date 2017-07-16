package demo4;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by jet on 2017/7/16.
 */
public class Searcher {

    //这个方法是搜索索引的方法，传入索引路径和查询表达式
    public static void search(String indexDir,String query) throws IOException, ParseException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
        QueryParser parser = new QueryParser("contents",new StandardAnalyzer());
        Query q = parser.parse(query);
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(q,10);
        long end = System.currentTimeMillis();
        System.out.println(hits.totalHits);
        System.out.println("how long it takes to search: "+(end-start));
        for (ScoreDoc scoredoc:hits.scoreDocs
             ) {
            Document doc = searcher.doc(scoredoc.doc);
            System.out.println(doc.get("fullpath"));
        }


    }

    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "E:/index";
        String query = "netty";
        search(indexDir,query);
    }

}
