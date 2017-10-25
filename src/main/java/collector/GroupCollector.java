package collector;

import com.pachira.psae.common.CollectionUtils;
import com.pachira.psae.common.StringUtils;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

/**
 * 分组统计的Collector
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @see
 * @since     1.0
 */
public class GroupCollector extends ParallelCollector {

    /**
     * 分组字段Set
     */
    private Set<String> groupFieldSet = new HashSet<String>();

    /**
     * 记录统计字段的所有值
     */
    private Object countValues;

    /**
     * 记录分组字段的所有值
     */
    private Object groupValues;

    /**
     * 分组字段docId缓存
     */
    private Bits bits;

    /**
     * 统计字段
     */
    private IndexField countField;

    /**
     * 分组字段
     */
    private IndexField groupField;

    /**
     * 分组区间
     */
    private List<GroupRange> groupRangeList;

    /**
     * 分组匹配模式
     */
    private String groupMatchMode;

    /**
     * 允许的最大组数
     */
    private int maxGroupSize;

    /**
     * 分组统计结果
     */
    private Map<String, Group> groupMap = new HashMap<String, Group>();

    public void setCountField(IndexField countField) {
        this.countField = countField;
    }

    public void setGroupField(IndexField groupField) {
        this.groupField = groupField;
        groupFieldSet.add(groupField.getName());
    }

    public void setGroupRangeList(List<GroupRange> groupRangeList) {
        this.groupRangeList = groupRangeList;
    }

    public void setGroupMatchMode(String groupMatchMode) {
        this.groupMatchMode = groupMatchMode;
    }

    public void setMaxGroupSize(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public Map<String, Group> getGroupMap() {
        return groupMap;
    }

    @Override
    public void collect(int doc) throws IOException {
        //超过最大组数直接返回
        if(groupMap.size() > maxGroupSize){
            return;
        }

        //统计值
        float countValue = getCountValue(doc);

        //分组值
        String groupValue = getGroupValue(doc).toString();
        if(!StringUtils.isAbsEmpty(groupValue)) {
            if (CollectionUtils.isAbsEmpty(groupRangeList)) {
                //按值分组
                addGroup(groupValue, countValue);
            } else {
                //区间分组
                for (GroupRange groupRange : groupRangeList) {
                    String type = groupField.getType();
                    String min = groupRange.getMin();
                    String max = groupRange.getMax();

                    if (type.equals(IndexField.TYPE_INT) || type.equals(IndexField.TYPE_FLOAT)) {
                        float value = Float.valueOf(groupValue);

                        //极限值不是数值型，跳过
                        if (!StringUtils.isValidFloat(min) || !StringUtils.isValidFloat(max)) {
                            continue;
                        }

                        if (value >= Float.valueOf(min) && value <= Float.valueOf(max)) {
                            addGroup(groupRange.getName(), countValue);
                        }
                    } else {
                        if (groupValue.compareTo(min) >= 0 && groupValue.compareTo(max) <= 0) {
                            addGroup(groupRange.getName(), countValue);
                        }
                    }
                }
            }
        }
       docCount++;
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        this.setNextReader(context);
        return super.getLeafCollector(context);
    }

    public void setNextReader(LeafReaderContext context) throws IOException {
        reader = context.reader();
        if (countField != null) {
                countValues = (NumericDocValues)reader.getNumericDocValues(countField.getName());
            }

        if (groupField != null) {
            groupValues =(SortedDocValues)reader.getSortedDocValues(groupField.getName());
            bits = reader.getDocsWithField( groupField.getName());
        }
    }

    @Override
    public ParallelCollector clone() {
        GroupCollector groupCollector = new GroupCollector();
        groupCollector.setCountField(countField);
        groupCollector.setGroupField(groupField);
        groupCollector.setGroupRangeList(groupRangeList);
        groupCollector.setGroupMatchMode(groupMatchMode);
        groupCollector.setMaxGroupSize(maxGroupSize);

        return groupCollector;
    }

    @Override
    public void merge(ParallelCollector collector) {
        synchronized (this) {
            if(!(collector instanceof GroupCollector)) {
                return;
            }

            GroupCollector groupCollector = (GroupCollector)collector;
            Map<String, Group> cloneGroupMap = groupCollector.getGroupMap();
            docCount += groupCollector.getDocCount();

            for (String key : cloneGroupMap.keySet()) {
                Group cloneGroup = cloneGroupMap.get(key);
                if (groupMap.containsKey(key)) {
                    Group group = groupMap.get(key);
                    group.setCount(group.getCount() + cloneGroup.getCount());
                    group.setSum(group.getSum() + cloneGroup.getSum());

                    if (cloneGroup.getMax() > group.getMax()) {
                        group.setMax(cloneGroup.getMax());
                    }

                    if (cloneGroup.getMin() < group.getMin()) {
                        group.setMin(cloneGroup.getMin());
                    }
                } else {
                    groupMap.put(key, cloneGroup);
                }
            }

            groupCollector.getGroupMap().clear();
        }
    }

    /**
     * 获取统计字段值
     */
    private float getCountValue(int doc) throws IOException{
        if (countValues != null) {
                return ((NumericDocValues) countValues).get(doc);
        }
        return 0;
    }

    /**
     * 获取分组字段值
     */
    private Object getGroupValue(int doc) throws IOException{
        if (groupValues != null) {
            if(bits.get(doc)) {
                    return ((NumericDocValues) groupValues).get(doc);
                } else {
                    BytesRef bytesRef = ((SortedDocValues)groupValues).get(doc);
                    return formatGroupValue(bytesRef.utf8ToString());
                }
            }
        return "";
    }

    /**
     * 格式化分组字段值
     */
    private String formatGroupValue(String value) throws IOException{
        if (groupField.getType().equals(IndexField.TYPE_DATE)) {
            if (groupMatchMode.equalsIgnoreCase(SearchRequest.GROUP_MATCH_MODE_YEAR)) {
                value = value.substring(0, 4);
            } else if (groupMatchMode.equalsIgnoreCase(SearchRequest.GROUP_MATCH_MODE_MONTH)) {
                value = value.substring(5, 7);
            } else if (groupMatchMode.equalsIgnoreCase(SearchRequest.GROUP_MATCH_MODE_WEEK)) {
                value = value.substring(24);
            } else if (groupMatchMode.equalsIgnoreCase(SearchRequest.GROUP_MATCH_MODE_HOUR)) {
                value = value.substring(11, 13);
            } else {
                value = value.substring(0, 23);
            }
        }

        return value;
    }

    /**
     * 添加一个分组
     */
    private void addGroup(String key, float value) {
        Group group = groupMap.get(key);
        if (group == null) {
            group = new Group();
            group.setName(key);
            groupMap.put(key, group);
        }

        group.setCount(group.getCount() + 1);

        if (countValues != null) {
            group.setSum(group.getSum() + value);

            if (group.getMax() == 0 || value > group.getMax()) {
                group.setMax(value);
            }

            if (group.getMin() == 0 || value < group.getMin()) {
                group.setMin(value);
            }
        }
    }
}
