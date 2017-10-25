package FilterQueryTest;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 17:10  2017/8/31
 * @Modified By:
 */
public class SpanFilterQuery {

    private Query query;              //最终需要的查询对象
    private SpanQuery[] spanQuery;
    private SpanNearQuery[] spanNearQueries;
    private List<String[]> words = new ArrayList<String[]>();

    /**
     *@Author: Jet Hu
     *@Description: 跨度查询 Near and After
     *@Date: 21:45  2017/8/31
     *@param: field  分词字段
     *@param:  items 词组
     *@param:  analyzer 分词器，分为单字
     *@param: slop 词间距
     *@param: inOrder 为true则是after查询，false为 near查询
     */
    public SpanFilterQuery(String field, String[] items, String analyzer , int slop, boolean inOrder) {
        for(String item : items) {
//            List<String> valueList = AnalyzerUtils.segmentText(item, analyzer);
//            words.add(valueList.toArray(new String[0]));
        }
        spanNearQueries = new SpanNearQuery[words.size()];
        for (int j = 0; j < words.size(); j++) {
            for (int i = 0; i < words.get(j).length; i++) {
                spanQuery[i] = new SpanTermQuery(new Term(field,words.get(j)[i]));
            }
            spanNearQueries[j] = new SpanNearQuery(spanQuery,0,true);
        }
        query = new SpanNearQuery(spanNearQueries,slop,inOrder);
    }

    public Query getQuery() {
        return query;
    }
}
