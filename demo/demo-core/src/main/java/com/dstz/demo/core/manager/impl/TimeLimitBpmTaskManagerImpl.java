package com.dstz.demo.core.manager.impl;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.manager.impl.BaseManager;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.dao.TimeLimitBpmTaskDao;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.TimeLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("timeLimitBpmTaskManager")
public class TimeLimitBpmTaskManagerImpl extends BaseManager<String,TimeLimit> implements TimeLimitBpmTaskManager {

	@Autowired
	private TimeLimitBpmTaskDao timeLimitBpmTaskDao;

	@Override
	public void saveTimeLimitData(TimeLimit  timeLimitData) {
		 this.timeLimitBpmTaskDao.saveTimeLimitData(timeLimitData);
	}

	@Override
	public TimeLimit getTimeLimitData(TimeLimit timeLimitData) {
		TimeLimit resultTimeLimit = this.timeLimitBpmTaskDao.getTimeLimitData(timeLimitData);
		return resultTimeLimit;
	}

	@Override
	public void updateTimeLimitData(TimeLimit  timeLimitData) {
		timeLimitBpmTaskDao.updateTimeLimitData(timeLimitData);
	}

	@Override
	public List<Map<String, Object>> getUserTodoData(String userId) {
		return timeLimitBpmTaskDao.getUserTodoData(userId);
	}

	@Override
	public List<TimeLimit> getTimeLimitList(List<BpmTask> listTodoTask) {
		if(listTodoTask!=null&&listTodoTask.size()>0){
			List<String> taskIds =new ArrayList<String>();
			for (BpmTask param: listTodoTask) {
				taskIds.add(param.getId());
			}
			return timeLimitBpmTaskDao.getTimeLimitList(taskIds);
		}else{
			return null;
		}
	}

	@Override
	public List<TimeLimit> getDelayTaskList(QueryFilter queryFilter) {
		return timeLimitBpmTaskDao.getDelayTaskList(queryFilter);
	}

	@Override
	public void updateDelayTask(TimeLimit  timeLimitData) {
		timeLimitBpmTaskDao.updateDelayTask(timeLimitData);
	}

}
