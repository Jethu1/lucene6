package demo1;

/**
 * Created by Administrator on 2017/6/17.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.nio.file.FileSystems;


public   class  TestQuery  {
    public   static   void  main(String[] args)  throws  Exception {

        String queryString  = "密码" ;
        Query query  =   null ;
        Directory indexDir  =   NIOFSDirectory.open(FileSystems.getDefault().getPath("d://index"));
        IndexSearcher searcher  =   new IndexSearcher(DirectoryReader.open(indexDir));
        Analyzer analyzer  =   new StandardAnalyzer();
        query  = new QueryParser("body", analyzer).parse("密码");
        System.out.println("命中个数:"+searcher.search(query, 10).totalHits);

        TopDocs topDocs = searcher.search(query,10);
        ScoreDoc[] hits = topDocs.scoreDocs;

        for (int i = 0; i <hits.length ; i++) {
            Document doc = searcher.doc(hits[i].doc);
            System.out.println(doc.get("body"));

        }
/*        try   {
            QueryParser qp  =   new  QueryParser( "body" , analyzer);
            query  =  qp.parse(queryString);
        }   catch  (ParseException e)  {
        }
        if  (searcher  !=   null )  {
            hits  =  searcher.search(query);
            if  (hits.length()  >   0 )  {
                System.out.println( "找到: "   +  hits.length()  +   "  个结果! " );
            }
        }

        for (int i = 0; i <hits.length() ; i++) {
            org.apache.lucene.document.Document document = hits.doc(i);
            System.out.println("内容"+document.get("body"));
            System.out.println("路径:"+document.get("path"));
            }*/
        }

    }

