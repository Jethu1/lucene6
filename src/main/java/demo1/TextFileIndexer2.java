package demo1;

/**
 * Created by Administrator on 2017/6/17.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.Date;

/** */

/**
 * author lighter date 2006-8-7
 */
public   class  TextFileIndexer2  {
    public   static   void  main(String[] args)  throws  Exception  {
         /**/ /* ?????????????��?? */
        File fileDir  =   new  File( "d://develop" );
//         File file = new File("d://index");
//??????????��??
        Directory indexDir  =   NIOFSDirectory.open(FileSystems.getDefault().getPath("d://index"));
        Analyzer luceneAnalyzer  =   new StandardAnalyzer();  //????????????????
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(luceneAnalyzer);
        IndexWriter indexWriter  =   new IndexWriter(indexDir, indexWriterConfig);   //?????????????
        File[] textFiles  =  fileDir.listFiles();
        long  startTime  =   new  Date().getTime();

        //????document???????
        for  ( int  i  =   0 ; i  <  textFiles.length; i ++ )  {
            if  (textFiles[i].isFile()
                    &&  textFiles[i].getName().endsWith( ".txt" ))  {
                System.out.println( " File "   +  textFiles[i].getCanonicalPath()
                        +   "???��??. " );
                String temp  =  FileReaderAll(textFiles[i].getCanonicalPath(),"GBK" );
                System.out.println(temp);
                Document document  =   new Document();
                Field FieldPath  =   new Field( "path" , textFiles[i].getPath(), TextField.TYPE_STORED);
                Field FieldBody  =   new Field( "body" , temp, TextField.TYPE_STORED);
                document.add(FieldPath);
                document.add(FieldBody);
                indexWriter.addDocument(document);
            }
        }

        indexWriter.commit();
        indexWriter.close();


        long  endTime  =   new  Date().getTime();
        System.out.println( "??????-??????? "
                        +  (endTime  -  startTime)
                        +   "  ???? "
                        +  fileDir.getPath());
    }

    public   static  String FileReaderAll(String FileName, String charset)
            throws  IOException  {
        BufferedReader reader  =   new  BufferedReader( new  InputStreamReader(
                new  FileInputStream(FileName), charset));
        String line  =   new  String();
        String temp  =   new  String();

        while  ((line  =  reader.readLine())  !=   null )  {
            temp  +=  line;
        }
        reader.close();
        return  temp;
    }
}