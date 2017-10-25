package log4j;

/**
 * @Author: Jet Hu
 * @Description:
 * @Date: Created in 14:10  2017/9/2
 * @Modified By:
 */
public class ArrayTest {

    public static void main(String[] args) {
        int[] arr = new int[10];
        arr[1] = 12;
        for (int s:arr
             ) {
            System.out.println(s);
        }
    }

}
