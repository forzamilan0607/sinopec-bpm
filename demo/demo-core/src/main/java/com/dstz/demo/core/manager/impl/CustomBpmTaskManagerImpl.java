package com.dstz.demo.core.manager.impl;

import com.dstz.base.manager.impl.BaseManager;
import com.dstz.demo.core.dao.CustomBpmTaskDao;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
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
}