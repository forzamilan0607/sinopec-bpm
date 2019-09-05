package com.dstz.demo.core.manager;

import com.dstz.demo.core.model.dto.BpmTaskDTO;

import java.util.List;

public interface CustomBpmTaskManager {

    List<BpmTaskDTO> queryListByTaskIds(List<String> taskIds);
}
