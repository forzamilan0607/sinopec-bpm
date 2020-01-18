package com.dstz.demo.core.manager;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.demo.core.model.dto.TaskCountDTO;

import java.util.List;

public interface CustomBpmTaskManager {

    List<BpmTaskDTO> queryListByTaskIds(List<String> taskIds);

    List<String> queryTaskNames(String userId);

    TaskCountDTO countTodoAndDelayTasks(String userId);

    List<BpmTaskNew> queryBpmTaskNewList(QueryFilter queryFilter);

    List<BpmTaskNew> queryToDoList(QueryFilter queryFilter);
}
