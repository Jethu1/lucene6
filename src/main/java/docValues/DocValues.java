package docValues;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 16:09  2017/9/20
 * @Modified By:
 */

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import java.io.IOException;

public class DocValues {
    static final String NUMERIC_FIELD = "numeric";
    static final String FLOATS_FIELD = "float";
    static final String SORT_NUMERIC_FIELD = "sort_numeric";
    static final String BINARY_FIELD = "binary";
    static final String SORTED_FIELD = "sorted";
    static final String SORTEDSET_FIELD = "sortedset";

    static long[] numericVals = new long[] {12, 13, 0, 100, 19};
    static float[] floatsVals = new float[]{1.5f,3.6f,4.5f,5.6f,7.8f};
    static String[] binary = new String[] {"lucene", "doc", "value", "test", "example"};
    static String[] sortedVals = new String[] {"lucene", "facet", "abacus", "search", null};
    static String[][] sortedSetVals = new String[][] {{"lucene", "search"}, {"search"}, {"facet", "abacus", "search"}, {}, {}};

    static IndexReader topReader;
    static LeafReader atomicReader;


    public static void main(String[] args) throws IOException {
        RAMDirectory dir = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig( new   StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        for (int i = 0; i < numericVals.length; ++i) {
            Document doc = new Document();
            doc.add(new NumericDocValuesField(NUMERIC_FIELD, numericVals[i]));
            doc.add(new NumericDocValuesField(FLOATS_FIELD, NumericUtils.floatToSortableInt(floatsVals[i])));
            doc.add(new SortedNumericDocValuesField(SORT_NUMERIC_FIELD, numericVals[i]));
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
        System.out.println(docVals1.get(2));

        NumericDocValues floatVals = atomicReader.getNumericDocValues(FLOATS_FIELD);
        System.out.println(floatVals.get(1));

        BinaryDocValues docVals2 = atomicReader.getBinaryDocValues(BINARY_FIELD);
        BytesRef bytesRef = docVals2.get(0);
        System.out.println(bytesRef.utf8ToString());

        SortedNumericDocValues docVals3 = atomicReader.getSortedNumericDocValues(SORT_NUMERIC_FIELD);
        String ordInfo = "", values = "";
        for (int i = 0; i < atomicReader.maxDoc(); ++i) {
            ordInfo = docVals3.valueAt(i) + ":";
            values += ordInfo + " : ";
        }
        //2:1:0:3:-1
        //lucene:facet:abacus:search::
        System.out.println(values);

        SortedDocValues docVals4 = atomicReader.getSortedDocValues(SORTED_FIELD);
        String ordInfo1 = "", values1 = "";
        for (int i = 0; i < atomicReader.maxDoc(); ++i) {
            ordInfo1 += docVals4.getOrd(i) + ":";
            bytesRef = docVals4.get(i);
            values1 += bytesRef.utf8ToString() + ":";
        }
        //2:1:0:3:-1
        System.out.println(ordInfo1);
        //lucene:facet:abacus:search::
        System.out.println(values1);


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
