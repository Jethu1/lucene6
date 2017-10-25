package FilterToQuery2;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.util.BytesRef;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 17:31  2017/9/22
 * @Modified By:
 */
public class IntPointTest {
    public static void main(String[] args) {
        int a =2;
        byte[] b = new byte[16];
        BytesRef byteRef = new BytesRef("3");
        System.out.println(byteRef.bytes);
        IntPoint.encodeDimension(3,b,0);
        System.out.println("1");
    }
}
