package com.dstz.demo.core.manager;


import com.dstz.base.api.query.QueryFilter;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.demo.core.model.dto.DelayTaskCountDTO;

import java.util.List;
import java.util.Map;

public interface TimeLimitBpmTaskManager {

    public void saveTimeLimitData(TimeLimit timeLimitDate);
    public TimeLimit getTimeLimitData(TimeLimit timeLimitDate);
    public void updateDelayTask(TimeLimit timeLimitDate);
    public void updateTimeLimitData(TimeLimit timeLimitDate);
    public List<Map<String,Object>> getUserTodoData(String userId);

    List<TimeLimit> getTimeLimitList(List<BpmTask> listTodoTask);

    List<TimeLimit> getDelayTaskList(QueryFilter queryFilter);

    List<DelayTaskCountDTO> queryDelayTasksGroupByMaterialNo(QueryFilter queryFilter);
}
