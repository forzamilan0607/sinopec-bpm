package com.dstz.demo.core.manager;

import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.demo.core.model.dto.TaskCountDTO;

import java.util.List;

public interface CustomBpmTaskManager {

    List<BpmTaskDTO> queryListByTaskIds(List<String> taskIds);

    List<String> queryTaskNames(String userId);

    TaskCountDTO countTodoAndDelayTasks(String userId);
}
