package demo4;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by jet on 2017/7/16.
 */
public class Indexer {

    private IndexWriter writer;//这个类用来写入索引


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
    }
/*
* Directory dir=FSDirectory.open(Paths.get(indexDir));这个方法是获取索引存放路径的方法，
* 早期版本的Lucene在open方法中用new File或new一个输入流的方式获取路径，
* 目前这里采用的是nio的方式获取文件夹路径，效率较高。
IndexWriterConfig config=new IndexWriterConfig(new StandardAnalyzer());
config.setOpenMode(OpenMode.CREATE_OR_APPEND);
writer=new IndexWriter(dir,config);
这里IndexWriterConfig对象是IndexWriter的配置对象，有很多配置。第二行的openMode是索引路径打开的方式，
这里选择的是创建或追加内容。
* */
    //关闭indexWriter
    public void close() throws IOException {
        writer.close();
    }

    //这个方法是遍历文件夹下所有文件并选择符合条件文件写入索引的方法，返回写入的文档总数
    public int index(String dataDir,FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        for (File file:files
             ) {
            if(!file.isDirectory()&&!file.isHidden()&&file.exists()
                    &&file.canRead()&&(filter==null)||filter.accept(file))
                indexFile(file);
            else
                System.out.println("can not find files or other problems");
        }
        return writer.numDocs();
    }

    //这个方法是写入索引的方法，将生成的document对象写入到索引中
    private void indexFile(File file) throws IOException {
        System.out.println("indexing..."+file.getCanonicalPath());
        Document doc = getDocument(file);
        writer.addDocument(doc);
    }

    //这个方法是生成Document对象的方法，Document对象就是对文档各个属性的封装
    private Document getDocument(File file) throws IOException {
        Document doc = new Document();
        doc.add(new Field("contents",new FileReader(file), TextField.TYPE_NOT_STORED));//need to know more
        doc.add(new Field("fileName",file.getName(),TextField.TYPE_STORED));
        doc.add(new Field("fullpath",file.getCanonicalPath(), TextField.TYPE_STORED));//what's the meaning of getCanonicalPath()
        return doc;
    }

    public static void main(String[] args) throws IOException {
        String indexDir = "E:/index";// the dirctory that stores index.
        String dataDir = "E:/data";//the dirctory that stores files to be indexed.

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numDoc=indexer.index(dataDir,new TextFilesFilter());

        indexer.close();
        long end = System.currentTimeMillis();
        System.out.println("time cost to store index: " + (end-start));
        System.out.println("total number of documents: "+ numDoc);
    }

}
