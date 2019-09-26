package com.dstz.demo.rest.controller;

import com.dstz.agilebpm.component.j2cache.SpringContextUtils;
import com.dstz.bpm.api.model.inst.IBpmInstance;
import com.dstz.bpm.api.model.task.IBpmTask;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.demo.core.utils.DemoUtils;
import com.dstz.sys.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class TimeLimitBpmTaskHandler {
    private static Logger logger = LoggerFactory.getLogger(TimeLimitBpmTaskHandler.class);


    /**
     * 流程前置事件  配置时间限制
     *
     * @param bpmInstance
     * @param bpmTask
     * @param timeLimitStr
     * @return
     */
    public static String getTimeLimit(IBpmInstance bpmInstance, IBpmTask bpmTask, String timeLimitStr) {
        //得到当前流程的当前环节的时间限制值
        TimeLimitBpmTaskManager staticTimeLimitBpmTaskManager = SpringContextUtils.getBean(TimeLimitBpmTaskManager.class);
        Date createTime = new Date();
        Date taskEndTime = DemoUtils.getExpectDealTime(createTime, timeLimitStr);
        logger.debug("timeLimitStr=" + timeLimitStr + ", bpmInstance=" + bpmInstance.getActInstId() + ", taskEndTime=" + taskEndTime);
        TimeLimit timeLimit = new TimeLimit();
        timeLimit.setId(UUID.randomUUID().toString());
        timeLimit.setName(bpmTask.getName());
        timeLimit.setSubject(bpmTask.getSubject());
        timeLimit.setInstId(bpmInstance.getId());
        timeLimit.setTaskId(bpmTask.getId());
        timeLimit.setTimeLimit(timeLimitStr);
        timeLimit.setCreateTime(createTime);
        timeLimit.setCreatBy(ContextUtil.getCurrentUserId());
        timeLimit.setTaskStartTime(createTime);
        timeLimit.setTaskEndTime(taskEndTime);
        timeLimit.setAssigneeNames(bpmTask.getAssigneeNames());
        staticTimeLimitBpmTaskManager.saveTimeLimitData(timeLimit);
        return timeLimitStr;
    }

    /**
     * 流程后置事件  更新流程节点处理时间
     *
     * @param bpmInstance
     * @param bpmTask
     */
    public static void dealProcessTimeLimit(IBpmInstance bpmInstance, IBpmTask bpmTask) {
        TimeLimitBpmTaskManager staticTimeLimitBpmTaskManager = SpringContextUtils.getBean(TimeLimitBpmTaskManager.class);
        TimeLimit timeLimitDate = new TimeLimit();
        timeLimitDate.setInstId(bpmInstance.getId());
        timeLimitDate.setTaskId(bpmTask.getId());
        TimeLimit resultTimeLimit = staticTimeLimitBpmTaskManager.getTimeLimitData(timeLimitDate);
        timeLimitDate.setId(resultTimeLimit.getId());
        timeLimitDate.setTaskDealTime(new Date());
        staticTimeLimitBpmTaskManager.updateTimeLimitData(timeLimitDate);
    }


}
