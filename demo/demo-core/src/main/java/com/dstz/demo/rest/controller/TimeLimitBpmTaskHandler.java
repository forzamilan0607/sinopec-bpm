package com.dstz.demo.rest.controller;

import com.dstz.agilebpm.component.j2cache.SpringContextUtils;
import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.db.model.page.PageResult;
import com.dstz.base.rest.ControllerTools;
import com.dstz.base.rest.util.RequestUtil;
import com.dstz.bpm.api.model.inst.IBpmInstance;
import com.dstz.bpm.api.model.task.IBpmTask;
import com.dstz.bpm.core.manager.BpmTaskManager;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.demo.core.utils.DemoUtils;
import com.dstz.sys.util.ContextUtil;
import com.github.pagehelper.Page;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class TimeLimitBpmTaskHandler extends ControllerTools {
    private Logger logger = LoggerFactory.getLogger(TimeLimitBpmTaskHandler.class);

    @Autowired
    private TimeLimitBpmTaskManager timeLimitBpmTaskManager;

    @Autowired
    private BpmTaskManager bpmTaskManager;

    @RequestMapping("/bpm/task/getDelayTaskList")
    public PageResult getDelayTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<TimeLimit> timeLimitList = new Page<>();
        QueryFilter queryFilter = super.getQueryFilter(request);
        List<TimeLimit> resultTimeLimitList = timeLimitBpmTaskManager.getDelayTaskList(queryFilter);
        timeLimitList = getBuildTimeLimitList(resultTimeLimitList);
        Page<TimeLimit> pageList = (Page) timeLimitList;
        return new PageResult(pageList);
    }

    private List<TimeLimit> getBuildTimeLimitList(List<TimeLimit> resultTimeLimitList) {
        List<TimeLimit> param =new Page<>();
        if(resultTimeLimitList!=null&&resultTimeLimitList.size()>0){
            for (TimeLimit param1:resultTimeLimitList) {
                //是否延期了
                param1.setDelayFlag(getDelayFlag(param1.getTaskStartTime(),param1.getTimeLimit(),param1.getTaskDealTime(),param1).getDelayFlag());
                //延期时间段是多少
                param1.setDelayTimePeriod(getDelayFlag(param1.getTaskStartTime(),param1.getTimeLimit(),param1.getTaskDealTime(),param1).getDelayTimePeriod());
                param.add(param1);
            }
        }
        return param;
    }

    private String getDelayTimePeriod(Date startTime, Date endTime) {
        String delayTimePeriod =null;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000*24*60*60;
        //一天的毫秒数
        long nh = 1000*60*60;
        //一小时的毫秒数
        long nm = 1000*60;
        //一分钟的毫秒数
        long ns = 1000;
        //一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = endTime.getTime() - startTime.getTime();
        long day = diff/nd;
        //计算差多少天
        long hour = diff%nd/nh;
        //计算差多少小时
        long min = diff%nd%nh/nm;
        //计算差多少分钟
        long sec = diff%nd%nh%nm/ns;
        delayTimePeriod =day+"天"+hour+"小时"+min+"分钟"+sec+"秒";
        return delayTimePeriod;
    }

    private TimeLimit getDelayFlag(Date taskStartTime, String timeLimit, Date taskDealTime,TimeLimit timeLimitParam) {
        Date exportDealTime =getTaskEndTime(taskStartTime,timeLimit);
        Date now =new Date();
        if(taskDealTime==null){
            timeLimitParam.setDelayFlag(exportDealTime.before(new Date()));
            if(exportDealTime.before(now)){
                timeLimitParam.setDelayTimePeriod(getDelayTimePeriod(exportDealTime,now));
            }else{
                timeLimitParam.setDelayTimePeriod("");
            }
        }else {
            timeLimitParam.setDelayFlag(exportDealTime.before(taskDealTime));
            if(exportDealTime.before(taskDealTime)){
                timeLimitParam.setDelayTimePeriod(getDelayTimePeriod(exportDealTime,taskDealTime));
            }else{
                timeLimitParam.setDelayTimePeriod("");
            }
        }
        return timeLimitParam;
    }

    @RequestMapping("/custom/my/todoTaskList")
    public PageResult popupMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<BpmTaskNew> bpmTaskNewList = new Page<>();
        QueryFilter queryFilter = super.getQueryFilter(request);
        List<BpmTask> listTodoTask = bpmTaskManager.getTodoList(ContextUtil.getCurrentUserId(), queryFilter);
        if (CollectionUtils.isNotEmpty(listTodoTask)) {
            List<TimeLimit> listTimeLimit = timeLimitBpmTaskManager.getTimeLimitList(listTodoTask);
            for (BpmTask task : listTodoTask) {
                if (!DemoUtils.addTaskNew(bpmTaskNewList, listTimeLimit, task)) {
                    bpmTaskNewList.add(BpmTaskNew.build(task));
                }
            }
        }
        Page<BpmTaskNew> pageList = (Page) bpmTaskNewList;
        return new PageResult(pageList);
    }

    @RequestMapping("/bpm/task/delayTask")
    public ResultMsg<String> delayTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String oldPassWorld = RequestUtil.getRQString(request, "oldPassword","旧密码必填");
//        String newPassword = RequestUtil.getRQString(request, "newPassword","新密码必填");
        TimeLimit timeLimitDate = new TimeLimit();
        timeLimitDate.setTaskId(RequestUtil.getRQString(request, "taskId"));
        TimeLimit resultTimeLimit = timeLimitBpmTaskManager.getTimeLimitData(timeLimitDate);
        timeLimitDate.setDelayReason(RequestUtil.getRQString(request, "reason"));
        Date taskEndTime = getTaskEndTime(resultTimeLimit.getTaskEndTime(), RequestUtil.getRQString(request, "delayTime"));
        timeLimitDate.setIsDelay(1);
        timeLimitDate.setDelayTime(RequestUtil.getRQString(request, "delayTime"));
        timeLimitDate.setTaskEndTime(taskEndTime);
        timeLimitDate.setId(resultTimeLimit.getId());
        timeLimitBpmTaskManager.updateDelayTask(timeLimitDate);
        return getSuccessResult("延期任务成功");
    }


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
        System.out.print("timeLimitStr=" + timeLimitStr);
        System.out.print("bpmInstance=" + bpmInstance.getActInstId());
//        System.out.print("bpmTask="+bpmTask.);
        Date createTime = new Date();
        Date taskEndTime = getTaskEndTime(createTime, timeLimitStr);
        TimeLimit timeLimitDate = new TimeLimit();
        timeLimitDate.setId(UUID.randomUUID().toString());
        timeLimitDate.setName(bpmTask.getName());
        timeLimitDate.setSubject(bpmTask.getSubject());
        timeLimitDate.setInstId(bpmInstance.getId());
        timeLimitDate.setTaskId(bpmTask.getId());
        timeLimitDate.setTimeLimit(timeLimitStr);
        timeLimitDate.setCreateTime(createTime);
        timeLimitDate.setTaskStartTime(createTime);
        timeLimitDate.setTaskEndTime(taskEndTime);
        System.out.print("taskEndTime=" + taskEndTime.toString());
        staticTimeLimitBpmTaskManager.saveTimeLimitData(timeLimitDate);

        return timeLimitStr;
    }

    public static Date getTaskEndTime(Date createTime, String timeLimitStr) {
        Date taskEndDate = null;
        if (timeLimitStr != null) {
            int day = timeLimitStr.contains("天") ? Integer.parseInt(timeLimitStr.substring(0, timeLimitStr.indexOf("天"))) : Integer.valueOf(timeLimitStr);
//            int hour =Integer.parseInt(timeLimitStr.substring(timeLimitStr.indexOf("天"),timeLimitStr.indexOf("时")));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTime);
            System.out.println(calendar.get(Calendar.DAY_OF_MONTH));//今天的日期
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);//让日期加1
            System.out.println(calendar.get(Calendar.DATE));//加1之后的日期Top
            taskEndDate = calendar.getTime();
        }
        return taskEndDate;
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
