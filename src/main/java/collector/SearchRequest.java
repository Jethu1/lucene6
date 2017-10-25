package collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 索引数据查询参数定义
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @see
 * @since     1.0
 */
public class SearchRequest {

    /**
     * 查询类型
     * 包含：基本查询、模型提取关键词、模型统计命中数、词频统计、分组统计、聚类
     */
    public static final String TYPE_SEARCH = "search";
    public static final String TYPE_MODEL_WORD = "modelWord";
    public static final String TYPE_MODEL_COUNT = "modelCount";//模型命中数统计
    public static final String TYPE_WORD_COUNT = "wordCount";//词频统计
    public static final String TYPE_GROUP_COUNT = "groupCount";//分组统计
    public static final String TYPE_CLUSTER = "cluster";


    /**
     * 分组统计时分组匹配方式，默认all（都不匹配时为默认值）
     */
    public static final String GROUP_MATCH_MODE_ALL = "all";
    public static final String GROUP_MATCH_MODE_YEAR = "year";
    public static final String GROUP_MATCH_MODE_MONTH = "month";
    public static final String GROUP_MATCH_MODE_WEEK = "week";
    public static final String GROUP_MATCH_MODE_HOUR = "hour";

    /**
     * 分组统计时分组排序方式，默认count（都不匹配时为默认值）
     */
    public static final String GROUP_ORDER_MODE_GROUP = "group";
    public static final String GROUP_ORDER_MODE_COUNT = "count";
    public static final String GROUP_ORDER_MODE_SUM = "sum";
    public static final String GROUP_ORDER_MODE_AVE = "ave";
    public static final String GROUP_ORDER_MODE_MAX = "max";
    public static final String GROUP_ORDER_MODE_MIN = "min";

    /**
     * 筛选条件类型
     */
    public static final String FILTER_TYPE_TREE = "tree";
    public static final String FILTER_TYPE_EXPRESSION = "expression";

    /**
     * 查询类型
     */
    private String type = "";

    /**
     * 索引ID
     */
    private String indexIDs;

    /**
     * 筛选条件
     */
    private String filter;

    /**
     * 筛选条件类型: tree(筛选树),expression(筛选表达式),默认为tree
     */
    private String filterType = FILTER_TYPE_TREE;

    /**
     * 分页页码
     */
    private int pageIndex;

    /**
     * 分页页大小
     */
    private int pageSize;

    /**
     * near/after前后词的词距，默认为10，代表near/after前后词之间的长度在10以内
     */
    private int slop = 10;

    /**
     * 加载字段
     */
    private String loadFields;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 降序排序
     */
    private boolean orderDesc = true;


    /**
     * 模型列表
     */
    private List<String> modelList = new ArrayList<String>();


    /**
     * 统计字段
     */
    private String countField;

    /**
     * 分组字段
     */
    private String groupField;

    /**
     * 分组区间
     */
    private List<GroupRange> groupRangeList;

    /**
     * 分组匹配方式，默认all（都不匹配时为默认值）
     */
    private String groupMatchMode = GROUP_MATCH_MODE_ALL;

    /**
     * 分组排序方式，默认count（都不匹配时为默认值）
     */
    private String groupOrderMode = GROUP_ORDER_MODE_COUNT;

    /**
     * 分组降序排序
     */
    private boolean groupOrderDesc = true;

    /**
     * 聚类字段
     */
    private String clusterField;

    /**
     * 聚类关键词
     */
    private String clusterWord;

    /**
     * 指定词频统计，原因挖掘，词语联想使用的角色，默认为mix
     */
    private String role = "mix";     //角色,包括A、B、mix

    /**
     * 是否提取模型命中关键词
     */
    private boolean containKeyword = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndexIDs() {
        return indexIDs;
    }

    public void setIndexIDs(String indexIDs) {
        this.indexIDs = indexIDs;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }

    public String getLoadFields() {
        return loadFields;
    }

    public void setLoadFields(String loadFields) {
        this.loadFields = loadFields;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public boolean isOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(boolean orderDesc) {
        this.orderDesc = orderDesc;
    }

    public List<String> getModelList() {
        return modelList;
    }

    public void setModelList(List<String> modelList) {
        this.modelList = modelList;
    }

    public String getCountField() {
        return countField;
    }

    public void setCountField(String countField) {
        this.countField = countField;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public List<GroupRange> getGroupRangeList() {
        return groupRangeList;
    }

    public void setGroupRangeList(List<GroupRange> groupRangeList) {
        this.groupRangeList = groupRangeList;
    }

    public String getGroupMatchMode() {
        return groupMatchMode;
    }

    public void setGroupMatchMode(String groupMatchMode) {
        this.groupMatchMode = groupMatchMode;
    }

    public String getGroupOrderMode() {
        return groupOrderMode;
    }

    public void setGroupOrderMode(String groupOrderMode) {
        this.groupOrderMode = groupOrderMode;
    }

    public boolean isGroupOrderDesc() {
        return groupOrderDesc;
    }

    public void setGroupOrderDesc(boolean groupOrderDesc) {
        this.groupOrderDesc = groupOrderDesc;
    }

    public String getClusterField() {
        return clusterField;
    }

    public void setClusterField(String clusterField) {
        this.clusterField = clusterField;
    }

    public String getClusterWord() {
        return clusterWord;
    }

    public void setClusterWord(String clusterWord) {
        this.clusterWord = clusterWord;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isContainKeyword() {
        return containKeyword;
    }

    public void setContainKeyword(boolean containKeyword) {
        this.containKeyword = containKeyword;
    }
}
