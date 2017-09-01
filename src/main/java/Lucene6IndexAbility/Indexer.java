package Lucene6IndexAbility;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by jet on 2017/7/16.
 */
public class Indexer {

    private static IndexWriter writer;//这个类用来写入索引
    static  String[] fileName;
    static  String[] fullPath;
    static  String[] contents;
    //下面这个类是FileFilter的实现类，用来过滤符合条件的文档。
    private static class TextFilesFilter implements FileFilter{
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }

    //构造方法，用来传入索引存放路径
    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
//        Directory dir = new  RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(dir,config);
        writer.forceMerge(5);
    }

    //关闭indexWriter
    public void close() throws IOException {
        writer.close();
    }

    //这个方法是遍历文件夹下所有文件并选择符合条件文件写入索引的方法，返回写入的文档总数
    public void index(String dataDir,FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        fileName = new String[files.length];
        fullPath = new String[files.length];
       contents= new String[files.length];
        int count=0;
        for (File file:files) {
            if(!file.isDirectory()&&!file.isHidden()&&file.exists()&&file.canRead()&&(filter==null)||filter.accept(file)) {
                fileName[count] = file.getName();
                fullPath[count] = file.getCanonicalPath();
                contents[count]=FileReaderAll(file.getCanonicalPath(),"UTF-8" );
                System.out.println(contents[count]);
            }
            else
                System.out.println("can not find files or other problems");
            count++;
        }
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

    public static void main(String[] args) throws IOException {
        String indexDir = "50index";// the dirctory that stores index.
        String dataDir = "data";//the dirctory that stores files to be indexed.

        Indexer indexer = new Indexer(indexDir);
        indexer.index(dataDir,new TextFilesFilter());
        long start = System.currentTimeMillis();
        for (int k = 0; k < 5000; k++) {
            Document[] docs = new Document[100];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    docs[j+i*10] = new Document();
                    docs[j+i*10].add(new Field("contents",contents[j], TextField.TYPE_NOT_STORED));
                    docs[j+i*10].add(new Field("fileName",fileName[j], TextField.TYPE_STORED));
                    docs[j+i*10].add(new Field("filePath",fullPath[j], TextField.TYPE_STORED));
                }
            }

            for (int i = 0; i <100 ; i++) {
                writer.addDocument(docs[i]);
            }
            writer.commit();
        }

        indexer.close();
        long end = System.currentTimeMillis();
        System.out.println("time cost to store index: " + (end-start));

    }

}
