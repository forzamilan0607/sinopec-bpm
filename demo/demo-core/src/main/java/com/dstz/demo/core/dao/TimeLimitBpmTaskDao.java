package com.dstz.demo.core.dao;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.demo.core.model.dto.DelayTaskCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface TimeLimitBpmTaskDao extends BaseDao<String, TimeLimit> {

    void saveTimeLimitData(TimeLimit timeLimitData);

    void updateTimeLimitData(TimeLimit timeLimitData);

    void updateDelayTask(TimeLimit timeLimitData);

    TimeLimit getTimeLimitData(TimeLimit timeLimitData);

    List<Map<String,Object>> getUserTodoData(String userId);

    List<TimeLimit> getTimeLimitList(@Param("taskIds") List<String> taskIds);

    List<TimeLimit> getDelayTaskList(QueryFilter queryFilter);

    /**
     * 根据实例ID查询对应的物料编码
     * @param instId
     * @return
     */
    String queryMaterialNoByInstId(String instId);

    /**
     * 以物料流程为维度查询延期任务
     * @param queryFilter
     * @return
     */
    List<DelayTaskCountDTO> queryDelayTasksGroupByMaterialNo(QueryFilter queryFilter);
}
