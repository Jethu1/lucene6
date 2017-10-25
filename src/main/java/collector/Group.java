package collector;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 组定义类
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @since     1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
public class Group {

    /**
     * 组名
     */
    private String name;

    /**
     * 文档数
     */
    private int count;

    /**
     * 总值
     */
    private float sum;

    /**
     * 平均值
     */
    private float ave;

    /**
     * 最大值
     */
    private float max;

    /**
     * 最小值
     */
    private float min;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public float getAve() {
        return ave;
    }

    public void setAve(float ave) {
        this.ave = ave;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
