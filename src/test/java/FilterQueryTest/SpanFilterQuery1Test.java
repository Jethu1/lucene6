package FilterQueryTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 15:08  2017/9/1
 * @Modified By:
 */
public class SpanFilterQuery1Test {
    @Test
    public void getQuery() throws Exception {

    }
     @Test
    public void segment() throws Exception {
         String item = "我是谁";
         List<String> list = new ArrayList<String>();
         for (int i = 0; i < item.length(); i++) {
             list.add(item.substring(i,i+1));
         }
         System.out.println(list);
         String[] lists= list.toArray(new String[0]);
         for (String s:lists
                 ) {
             System.out.println(s);
         }
    }


}