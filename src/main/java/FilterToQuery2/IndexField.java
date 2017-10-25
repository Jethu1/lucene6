package FilterToQuery2;

import java.io.Serializable;

/**
 * 索引字段定义
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @since     1.0
 */
public class IndexField implements Serializable {

    /**
     * 字段类型
     * 包含：int、float、date、string
     */
    public static final String TYPE_INT = "int";
    public static final String TYPE_FLOAT = "float";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_STRING = "string";

    /**
     * 索引字段名
     */
    private String name;

    /**
     * 索引字段类型
     */
    private String type;

    /**
     * 索引字段分词器名
     * 1.索引字段类型必须是STRING
     * 2.必须是TAE支持的分词器
     */
    private String analyzer;

    /**
     * 标识索引字段是否是多值字段，默认为false
     */
    private boolean multi = false;

    /**
     * 标记该字段是否可以用于聚类分析(一个索引结构中有且只有一个分词字段可以用做聚类分析字段)
     */
    private boolean clustered;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public boolean isClustered() {
        return clustered;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

   /* public String generateClusterField() {
        if(IndexField.TYPE_STRING.equals(type)
                && !StringUtils.isAbsEmpty(analyzer)
                && AnalyzerManager.enumAllAnalyzer().keySet().contains(analyzer)
                && AnalyzerManager.getFieldAnalyzer(analyzer).isText()){
            return "@" + this.getName() + "@";
        } else {
            return null;
        }
    }*/

    @Override
    public int hashCode() {
        StringBuffer attributes = new StringBuffer();
        attributes.append(name);
        attributes.append(type);
        if(analyzer != null) {
            attributes.append(analyzer);
        }
        attributes.append(multi);
        attributes.append(clustered);

        return attributes.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof IndexField) {
            if (this.hashCode() == obj.hashCode()) {
                return true;
            }
        }

        return false;
    }
}
