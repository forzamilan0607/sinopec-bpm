package com.dstz.demo.core.manager.impl;

import com.dstz.agilebpm.component.j2cache.SpringContextUtils;
import com.dstz.base.manager.impl.BaseManager;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.dao.TimeLimitBpmTaskDao;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.TimeLimit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("timeLimitBpmTaskManager")
public class TimeLimitBpmTaskManagerImpl extends BaseManager<String,TimeLimit> implements TimeLimitBpmTaskManager {


	@Override
	public void saveTimeLimitData(TimeLimit  timeLimitData) {
		 TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
		 timeLimitBpmTaskDao.saveTimeLimitData(timeLimitData);
	}

	@Override
	public TimeLimit getTimeLimitData(TimeLimit timeLimitData) {
		TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
		TimeLimit resultTimeLimit=timeLimitBpmTaskDao.getTimeLimitData(timeLimitData);
		return resultTimeLimit;
	}

	@Override
	public void updateTimeLimitData(TimeLimit  timeLimitData) {
		TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
		timeLimitBpmTaskDao.updateTimeLimitData(timeLimitData);
	}

	@Override
	public List<Map<String, Object>> getUserTodoData(String userId) {
		TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
		return timeLimitBpmTaskDao.getUserTodoData(userId);
	}

	@Override
	public List<TimeLimit> getTimeLimitList(List<BpmTask> listTodoTask) {
		TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
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
	public void updateDelayTask(TimeLimit  timeLimitData) {
		TimeLimitBpmTaskDao timeLimitBpmTaskDao = SpringContextUtils.getBean(TimeLimitBpmTaskDao.class);
		timeLimitBpmTaskDao.updateDelayTask(timeLimitData);
	}

}
