import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by jet on 2017/6/15.
 */

/*不做过多解释，但是有时候在项目开发过程中，突然心血来潮想把某些目录或文件加入忽略规则，
按照上述方法定义后发现并未生效，原因是.gitignore只能忽略那些原来没有被track的文件，
如果某些文件已经被纳入了版本管理中，则修改.gitignore是无效的。那么解决方法就是先把
本地缓存删除（改变成未track状态），然后再提交：

git rm -r --cached .
git add .
git commit -m 'update .gitignore'*/

public class SimpleExample {
    @Test
    public void main1() throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();

        // Store the index in memory:
        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        Document doc = new Document();
        String text = "This is the text to be indexed.";
        doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
        iwriter.addDocument(doc);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("fieldname", analyzer);
        Query query = parser.parse("text");
        ScoreDoc[] hits = isearcher.search(query,  1000).scoreDocs;
        for (int i=0;i<hits.length;i++){
           Document dc = isearcher.doc(hits[i].doc);
            System.out.println(dc.get("fieldname"));
        }
          assertEquals(1, hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
        }
        ireader.close();
        directory.close();
    }
}

