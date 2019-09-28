package com.dstz.demo.core.manager.impl;

import com.dstz.base.manager.impl.BaseManager;
import com.dstz.demo.core.dao.CustomBpmTaskDao;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.demo.core.model.dto.TaskCountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customBpmTaskManager")
public class CustomBpmTaskManagerImpl extends BaseManager<String, BpmTaskDTO> implements CustomBpmTaskManager {

	@Autowired
	private CustomBpmTaskDao customBpmTaskDao;

	@Override
	public List<BpmTaskDTO> queryListByTaskIds(List<String> taskIds) {
		return this.customBpmTaskDao.queryListByTaskIds(taskIds);
	}

	@Override
	public List<String> queryTaskNames(String userId) {
		return this.customBpmTaskDao.queryTaskNames(userId);
	}

	@Override
	public TaskCountDTO countTodoAndDelayTasks(String userId) {
		return this.customBpmTaskDao.countTodoAndDelayTasks(userId);
	}
}
