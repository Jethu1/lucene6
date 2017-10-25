package FilterToQuery1;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * lucene搜索帮助类
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @since     1.0
 */
public class SearchUtils {

    private static Pattern freqPattern = Pattern.compile("(\\S+)\\s+(freq|offset)\\s+(\\d+)(,\\d+)?");
    private static Pattern nearPattern = Pattern.compile("(\\S+)\\s+(near|after)\\s+(\\S+)");


    /**
     * 提取三连after模型的命中位置
     */
    public static boolean findMultiAfterPosition(List<DocPosition[]> docPositions,
                                                 int slop, boolean inOrder, int start, int end) throws IOException {
        if (findFollowPosition(docPositions.get(0), start, end)
                && findFollowPosition(docPositions.get(1), start, end)
                && findFollowPosition(docPositions.get(2), start, end)
                && findSlopPosition(docPositions.get(0), docPositions.get(1), slop, inOrder, start, end)
                && findSlopPosition(docPositions.get(1), docPositions.get(2), slop, inOrder, start, end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算指定词是否在文档中出现(即指定词中的term位置是否依次相邻)
     * @param docPositions  term位置信息数组
     * @return                该词在文档中出现返回true,否则返回false
     * @throws IOException
     */
    public static boolean findFollowPosition(DocPosition[] docPositions, int start, int end) throws IOException {
        //如果位置信息为空，直接返回false
        if(docPositions == null || docPositions.length < 1) {
            return false;
        }

        //遍历第一个term的位置，计算相邻位置
        while (docPositions[0].nextPosition()
                && docPositions[0].position() >= start
                && docPositions[0].position() <= end) {
            boolean flag = true;
            for (int i = 1; i < docPositions.length; i++) {
                while (docPositions[i].position() <= docPositions[i - 1].position()) {
                    if (!docPositions[i].nextPosition()) {
                        break;
                    }
                }

                //如果后一个term和前一个term的位置相差不为1，说明两个term不相邻，直接跳出当前循环
                if (docPositions[i - 1].position() + 1 != docPositions[i].position()) {
                    flag = false;
                    break;
                }
            }

            //如果flag为true，说明已找到相邻的位置，直接返回true
            if (flag) {
                return true;
            }
        }

        return false;
    }

    /**
     * 计算两个词的距离是否在指定的距离之内
     * @return              在指定的距离内返回true，否则返回false
     * @throws IOException
     */
    public static boolean findSlopPosition(DocPosition[] firstDocPosition,
                                           DocPosition[] secondDocPosition,
                                           int span, boolean inOrder,
                                           int start, int end) throws IOException {
        //如果后一个词为空，直接返回true
        if(secondDocPosition == null || secondDocPosition.length < 1) {
            return true;
        }

        //计算前后词间距
        int slop = 0;
        if (firstDocPosition[0].position() > secondDocPosition[0].position()) {
            slop = firstDocPosition[0].position() - secondDocPosition[0].position() - secondDocPosition.length;
        } else {
            slop = secondDocPosition[0].position() - firstDocPosition[0].position() - firstDocPosition.length;
        }

        //如果大于指定的间距，则继续计算该位置之后是否有指定距离内的词
        while (slop > span || slop < 0) {
            if (firstDocPosition[firstDocPosition.length - 1].position() >= secondDocPosition[0].position()) {
                if (!findFollowPosition(secondDocPosition, start, end)) {
                    return false;
                }
            } else {
                if (!findFollowPosition(firstDocPosition, start, end)) {
                    return false;
                }
            }

            //计算词间距
            if (firstDocPosition[0].position() > secondDocPosition[0].position()) {
                slop = firstDocPosition[0].position() - secondDocPosition[0].position() - secondDocPosition.length;
            } else {
                slop = secondDocPosition[0].position() - firstDocPosition[0].position() - firstDocPosition.length;
            }
        }

        //如果词间距在指定的范围内,还需要根据inOrder参数判断前后词出现的顺序是否符合条件
        if (slop >= 0 && slop <= span) {
            if (inOrder) {
                //如果有序: 前词大于后词的顺序，继续查找; 否则直接返回true
                if (firstDocPosition[0].position() > secondDocPosition[0].position()) {
                    if (findFollowPosition(secondDocPosition, start, end)) {
                        //继续查找下一个位置
                        return findSlopPosition(firstDocPosition, secondDocPosition, span, inOrder, start, end);
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
