package com.dstz.demo.core.dao;

import com.dstz.base.dao.BaseDao;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.model.TimeLimit;

import java.util.List;
import java.util.Map;


public interface TimeLimitBpmTaskDao extends BaseDao<String, TimeLimit> {

    void saveTimeLimitData(TimeLimit timeLimitData);
    void updateTimeLimitData(TimeLimit timeLimitData);
    void updateDelayTask(TimeLimit timeLimitData);
    TimeLimit getTimeLimitData(TimeLimit timeLimitData);
    List<Map<String,Object>> getUserTodoData(String userId);
    List<TimeLimit> getTimeLimitList(List<String> taskIds);
}
