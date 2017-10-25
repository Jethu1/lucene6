package collector;

import com.pachira.psae.common.StringUtils;

/**
 * 分组区间定义
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @since     1.0
 */
public class GroupRange {

    /**
     * 区间名
     */
    private String name;

    /**
     * 区间最小值
     */
    private String min;

    /**
     * 区间最大值
     */
    private String max;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    /**
     * 分组区间是否有效
     */
    public boolean isValid(){
        if (StringUtils.isValidFloat(min) && StringUtils.isValidFloat(max)) {
            if (Float.valueOf(min) > Float.valueOf(max)) {
                return false;
            }
        } else {
            if (min.compareTo(max) > 0) {
                return false;
            }
        }

        return true;
    }

    public String toString(){
        return "name = " + name + "; min = " + min + "; max = " + max;
    }
}
