package demo5_big;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by jet on 2017/7/17.
 */

public class AnalyzerUtil {
    private static Analyzer analyzer;

    public static Analyzer getIkAnalyzer() {
        if (analyzer == null) {
            // 当为true时，分词器迚行最大词长切分 ；当为false时，分词器迚行最细粒度切
//            analyzer = new IKAnalyzer(true);
        }
        return analyzer;
    }
}