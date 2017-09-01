package log4j;

/**
 * Created by jet on 2017/7/17.
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test {


    private static Logger logger = LogManager.getLogger(Test.class.getName());

    public static void main(String[] args) {

        logger.trace("开始程序.");
        Hello hello= new Hello();
        for (int i = 0; i < 10000;i++){
        if (!hello.hello()) {
            logger.error("hello");
        }
        }
        logger.trace("退出程序.");
    }
}