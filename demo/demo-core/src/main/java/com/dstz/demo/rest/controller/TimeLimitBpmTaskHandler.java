package com.dstz.demo.rest.controller;

import com.dstz.agilebpm.component.j2cache.SpringContextUtils;
import com.dstz.base.api.query.QueryField;
import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.query.QueryOP;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.db.model.query.DefaultQueryFilter;
import com.dstz.base.rest.ControllerTools;
import com.dstz.base.rest.util.RequestUtil;
import com.dstz.bpm.api.model.inst.IBpmInstance;
import com.dstz.bpm.api.model.task.IBpmTask;
import com.dstz.bpm.core.manager.BpmTaskManager;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.sys.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class TimeLimitBpmTaskHandler extends ControllerTools {
    private Logger logger = LoggerFactory.getLogger(TimeLimitBpmTaskHandler.class);

    @Autowired
    private TimeLimitBpmTaskManager timeLimitBpmTaskManager;

    @Autowired
    private BpmTaskManager bpmTaskManager;

    @RequestMapping("/bpm/task/todoTaskList")
    public List<BpmTaskNew> popupMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String userId = RequestUtil.getRQString(request, "userId");
//        timeLimitBpmTaskManager.getUserTodoData(userId);
        List<BpmTaskNew> bpmTaskNewList=new ArrayList<>();
        QueryFilter queryFilter =new DefaultQueryFilter();
        List<BpmTask> listTodoTask = bpmTaskManager.getTodoList(ContextUtil.getCurrentUserId(),queryFilter);
        System.out.print(1);
        List<TimeLimit> listTimeLimit =timeLimitBpmTaskManager.getTimeLimitList(listTodoTask);
        if(bpmTaskNewList.size()>0&&listTimeLimit!=null&&listTimeLimit.size()>0){
            for (BpmTask param: listTodoTask) {
                for (TimeLimit param2:listTimeLimit) {
                    if(param.getId().equals(param2.getTaskId())){
                        BpmTaskNew bpmTaskNew =new BpmTaskNew();
                        BeanUtils.copyProperties(param,bpmTaskNew);
                        bpmTaskNew.setDelayFlag(param2.getTaskEndTime().before(new Date()));
                        break;
                    }
                }

            }
        }
        return bpmTaskNewList;
    }

    @RequestMapping("/bpm/task/delayTask")
    public ResultMsg<String> delayTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String oldPassWorld = RequestUtil.getRQString(request, "oldPassword","旧密码必填");
//        String newPassword = RequestUtil.getRQString(request, "newPassword","新密码必填");
        TimeLimit timeLimitDate =new TimeLimit();
        timeLimitDate.setTaskId(RequestUtil.getRQString(request, "taskId"));
        TimeLimit resultTimeLimit =timeLimitBpmTaskManager.getTimeLimitData(timeLimitDate);
        timeLimitDate.setDelayReason(RequestUtil.getRQString(request, "reason"));
        Date taskEndTime =getTaskEndTime(resultTimeLimit.getTaskEndTime(),RequestUtil.getRQString(request, "delayTime"));
        timeLimitDate.setIsDelay(1);
        timeLimitDate.setDelayTime(RequestUtil.getRQString(request, "delayTime"));
        timeLimitDate.setTaskEndTime(taskEndTime);
        timeLimitDate.setId(resultTimeLimit.getId());
        timeLimitBpmTaskManager.updateDelayTask(timeLimitDate);
        return getSuccessResult("延期任务成功");
    }


    /**
     * 流程前置事件  配置时间限制
     * @param bpmInstance
     * @param bpmTask
     * @param timeLimitStr
     * @return
     */
    public static String  getTimeLimit(IBpmInstance bpmInstance, IBpmTask bpmTask, String timeLimitStr){
        //得到当前流程的当前环节的时间限制值
        TimeLimitBpmTaskManager staticTimeLimitBpmTaskManager = SpringContextUtils.getBean(TimeLimitBpmTaskManager.class);
        System.out.print("timeLimitStr="+timeLimitStr);
        System.out.print("bpmInstance="+bpmInstance.getActInstId());
//        System.out.print("bpmTask="+bpmTask.);
        Date createTime=new Date();
        Date taskEndTime =getTaskEndTime(createTime,timeLimitStr);
        TimeLimit timeLimitDate =new TimeLimit();
        timeLimitDate.setId(UUID.randomUUID().toString());
        timeLimitDate.setInstId(bpmInstance.getId());
        timeLimitDate.setTaskId(bpmTask.getId());
        timeLimitDate.setTimeLimit(timeLimitStr);
        timeLimitDate.setCreateTime(createTime);
        timeLimitDate.setTaskStartTime(createTime);
        timeLimitDate.setTaskEndTime(taskEndTime);
        System.out.print("taskEndTime="+taskEndTime.toString());
        staticTimeLimitBpmTaskManager.saveTimeLimitData(timeLimitDate);
        
       return timeLimitStr;
    }
    public static Date getTaskEndTime(Date createTime,String timeLimitStr){
        Date taskEndDate =null;
        if(timeLimitStr!=null&&timeLimitStr.contains("天")){
            int day =Integer.parseInt(timeLimitStr.substring(0,timeLimitStr.indexOf("天")));
//            int hour =Integer.parseInt(timeLimitStr.substring(timeLimitStr.indexOf("天"),timeLimitStr.indexOf("时")));
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(createTime);
            System.out.println(calendar.get(Calendar.DAY_OF_MONTH));//今天的日期
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+day);//让日期加1
            System.out.println(calendar.get(Calendar.DATE));//加1之后的日期Top
             taskEndDate=calendar.getTime();
        }
        return taskEndDate;
    }

    /**
     * 流程后置事件  更新流程节点处理时间
     * @param bpmInstance
     * @param bpmTask
     */
    public static void dealProcessTimeLimit(IBpmInstance bpmInstance, IBpmTask bpmTask){
        TimeLimitBpmTaskManager staticTimeLimitBpmTaskManager = SpringContextUtils.getBean(TimeLimitBpmTaskManager.class);
        TimeLimit timeLimitDate =new TimeLimit();
        timeLimitDate.setInstId(bpmInstance.getId());
        timeLimitDate.setTaskId(bpmTask.getId());
        TimeLimit resultTimeLimit =staticTimeLimitBpmTaskManager.getTimeLimitData(timeLimitDate);
        timeLimitDate.setId(resultTimeLimit.getId());
        timeLimitDate.setTaskDealTime(new Date());
        staticTimeLimitBpmTaskManager.updateTimeLimitData(timeLimitDate);
    }


}
