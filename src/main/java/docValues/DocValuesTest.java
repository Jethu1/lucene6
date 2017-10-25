package docValues;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 16:44  2017/9/19
 * @Modified By:
 */
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class DocValuesTest {
    static final String NUMERIC_FIELD = "numeric";
    static final String BINARY_FIELD = "binary";
    static final String SORTED_FIELD = "sorted";
    static final String SORTEDSET_FIELD = "sortedset";

    static long[] numericVals = new long[] {12, 13, 0, 100, 19};
    static String[] binary = new String[] {"lucene", "doc", "value", "test", "example"};
    static String[] sortedVals = new String[] {"lucene", "facet", "abacus", "search", null};
    static String[][] sortedSetVals = new String[][] {{"lucene", "search"}, {"search"}, {"facet", "abacus", "search"}, {}, {}};

    static IndexReader topReader;
    static LeafReader atomicReader;


    public static void main(String[] args) throws IOException {
        float a  =  2.5f;
        long b = (long)a;
        System.out.println(b);
        RAMDirectory dir = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        for (int i = 0; i < numericVals.length; ++i) {
            Document doc = new Document();
            doc.add(new NumericDocValuesField(NUMERIC_FIELD, numericVals[i]));
            doc.add(new BinaryDocValuesField(BINARY_FIELD, new BytesRef(binary[i])));
            if (sortedVals[i] != null) {
                doc.add(new SortedDocValuesField(SORTED_FIELD, new BytesRef(sortedVals[i])));
            }
            for (String value : sortedSetVals[i]) {
                doc.add(new SortedSetDocValuesField(SORTEDSET_FIELD, new BytesRef(value)));
            }
            writer.addDocument(doc);
        }
        writer.forceMerge(1);
        writer.commit();
        writer.close();

        topReader = DirectoryReader.open(dir);
        atomicReader = topReader.leaves().get(0).reader();

        NumericDocValues docVals1 = atomicReader.getNumericDocValues(NUMERIC_FIELD);
        System.out.println(docVals1.get(0));

        BinaryDocValues docVals2 = atomicReader.getBinaryDocValues(BINARY_FIELD);
        BytesRef bytesRef = docVals2.get(0);
        System.out.println(bytesRef.utf8ToString());

        SortedDocValues docVals3 = atomicReader.getSortedDocValues(SORTED_FIELD);
        String ordInfo = "", values = "";
        for (int i = 0; i < atomicReader.maxDoc(); ++i) {
            ordInfo += docVals3.getOrd(i) + ":";
            bytesRef = docVals3.get(i);
            values += bytesRef.utf8ToString() + ":";
        }
        //2:1:0:3:-1
        System.out.println(ordInfo);
        //lucene:facet:abacus:search::
        System.out.println(values);


        SortedSetDocValues docVals = atomicReader.getSortedSetDocValues(SORTEDSET_FIELD);
        String info = "";
        for (int i = 0; i < atomicReader.maxDoc(); ++i) {
            docVals.setDocument(i);
            long ord;
            info += "Doc " + i;
            while ((ord = docVals.nextOrd()) != SortedSetDocValues.NO_MORE_ORDS) {
                info += ", " + ord + "/";
                bytesRef = docVals.lookupOrd(ord);
                info += bytesRef.utf8ToString();
            }
            info += ";";
        }
        //Doc 0, 2/lucene, 3/search;Doc 1, 3/search;Doc 2, 0/abacus, 1/facet, 3/search;Doc 3;Doc 4;
        System.out.println(info);
    }
}
