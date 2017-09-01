package Lucene6IndexAbility;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 17:10  2017/8/31
 * @Modified By:
 */
public class SpanFilterQuery {

    private Query query;              //最终需要的查询对象
    private SpanQuery[] first;            //前词(分词之后的数组)
    private SpanQuery[] second;           //后词(分词之后的数组)
    private SpanNearQuery[] spanNearQueries;

    /**
     *@Author: Jet Hu
     *@Description: 跨度查询 Near and After
     *@Date: 21:45  2017/8/31
     *@param: field  分词字段
     *@param:   str1 前词
     *@param:  str2 后词
     *@param: slop 词间距
     *@param: inOrder 为true则是after查询，false为 near查询
     */
    public SpanFilterQuery(String field, String[] str1, String[] str2, int slop, boolean inOrder) {
        first = new SpanQuery[str1.length];
        second = new SpanQuery[str2.length];
        for (int i = 0; i < str1.length; i++) {
            first[i] = new SpanTermQuery(new Term(field,str1[i]));
        }
        for (int j = 0; j < str2.length; j++) {
            second[j] = new SpanTermQuery(new Term(field,str2[j]));
        }
        spanNearQueries = new SpanNearQuery[2];
        spanNearQueries[0] = new SpanNearQuery(first,0,true);
        spanNearQueries[1] = new SpanNearQuery(second,0,true);
        query = new SpanNearQuery(spanNearQueries,slop,inOrder);
    }

    /**
     *@Author: Jet Hu
     *@Description: 单个长词查询
     *@Date: 21:48  2017/8/31
     *@param:  str1 长词
     */
    public SpanFilterQuery(String field, String[] str1) {
        first = new SpanQuery[str1.length];
        for (int i = 0; i < str1.length; i++) {
            first[i] = new SpanTermQuery(new Term(field,str1[i]));
        }
        query = new SpanNearQuery(first,0,true);
    }

    public Query getQuery() {
        return query;
    }
}
