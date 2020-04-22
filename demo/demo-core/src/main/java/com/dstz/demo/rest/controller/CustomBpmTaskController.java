package com.dstz.demo.rest.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.query.QueryOP;
import com.dstz.base.api.query.WhereClause;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.db.model.page.PageResult;
import com.dstz.base.db.model.query.DefaultPage;
import com.dstz.base.db.model.query.DefaultQueryField;
import com.dstz.base.db.model.query.DefaultQueryFilter;
import com.dstz.base.rest.ControllerTools;
import com.dstz.base.rest.util.RequestUtil;
import com.dstz.bpm.api.engine.action.cmd.FlowRequestParam;
import com.dstz.bpm.core.manager.BpmTaskManager;
import com.dstz.bpm.core.manager.TaskIdentityLinkManager;
import com.dstz.bpm.engine.action.cmd.DefualtTaskActionCmd;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.manager.TimeLimitBpmTaskManager;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.core.model.TimeLimit;
import com.dstz.demo.core.model.dto.BpmTaskBatchDTO;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.demo.core.model.dto.DelayTaskCountDTO;
import com.dstz.demo.core.model.dto.TaskCountDTO;
import com.dstz.demo.core.utils.DemoConstants;
import com.dstz.demo.core.utils.DemoUtils;
import com.dstz.sys.util.ContextUtil;
import com.github.pagehelper.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class CustomBpmTaskController extends ControllerTools {
    private Logger logger = LoggerFactory.getLogger(CustomBpmTaskController.class);

    @Autowired
    private CustomBpmTaskManager customBpmTaskManager;

    @Autowired
    private BpmTaskManager bpmTaskManager;

    @Autowired
    private TimeLimitBpmTaskManager timeLimitBpmTaskManager;

    @Autowired
    private TaskIdentityLinkManager taskIdentityLinkManager;

    @RequestMapping("/bpm/task/getMultiTaskData")
    public ResultMsg<JSONObject> getMultiTaskData(HttpServletRequest request){
        List<String> taskIds = this.extractTaskIdsFromRequest(request);
        List<BpmTaskDTO> data = this.customBpmTaskManager.queryListByTaskIds(taskIds);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return super.getSuccessResult(jsonObject);
    }

    @RequestMapping("/bpm/task/doMultiAction")
    public ResultMsg<String> doAction4Tasks(@RequestBody BpmTaskBatchDTO param) throws Exception {
        return this.batchHandle(param);
    }

    private ResultMsg<String> batchHandle(@RequestBody BpmTaskBatchDTO param) {
        logger.error("params = " + param);
        param.getTaskList().forEach(item -> {
            FlowRequestParam flowReq = new FlowRequestParam();
            flowReq.setDefId(item.getDefId());
            flowReq.setInstanceId(item.getInstId());
            flowReq.setTaskId(item.getTaskId());
            flowReq.setAction(param.getAction());
            flowReq.setBusinessKey("");
//            flowReq.setNodeUsers(new JSONObject());
//            flowReq.setData(new JSONObject());
            flowReq.setFormType("INNER");
            flowReq.setOpinion(param.getOpinion());
            flowReq.setDestination("");
//            flowReq.setExtendConf(new JSONObject());
            DefualtTaskActionCmd taskModel = new DefualtTaskActionCmd(flowReq);
            taskModel.executeCmd();

        });
        logger.error("流程批量处理结束！");
        return this.getSuccessResult("批量处理成功");
    }

    @RequestMapping("/bpm/instance/doMultiAction")
    public ResultMsg<String> doAction4Instances(@RequestBody BpmTaskBatchDTO param) throws Exception {
        return this.batchHandle(param);
    }

    private List<String> extractTaskIdsFromRequest(HttpServletRequest request) {
        String taskIds = request.getParameter("taskIds");
        return Arrays.asList(taskIds.split(","));
    }

    @RequestMapping("/bpm/task/getTaskNames")
    public ResultMsg<JSONObject> getTaskNames(HttpServletRequest request){
        String type = request.getParameter("type");
        String userId = null;
        if (ObjectUtils.nullSafeEquals(type, "2")) {
            userId = ContextUtil.getCurrentUserId();
        }
        List<String> taskNames = this.customBpmTaskManager.queryTaskNames(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", taskNames);
        return super.getSuccessResult(jsonObject);
    }

    @RequestMapping("/bpm/task/getTaskList")
    public PageResult<BpmTaskNew> getTaskList(HttpServletRequest request, HttpServletResponse reponse) throws Exception{
        QueryFilter queryFilter = this.getQueryFilter(request);
       /* List<BpmTask> bpmTaskList = this.bpmTaskManager.query(queryFilter);
        List<BpmTaskNew> bpmTaskNewList = new Page<>();
        if (CollectionUtils.isNotEmpty(bpmTaskList)) {
            List<TimeLimit> listTimeLimit = timeLimitBpmTaskManager.getTimeLimitList(bpmTaskList);
            for (BpmTask task : bpmTaskList) {
                if (!DemoUtils.addTaskNew(bpmTaskNewList, listTimeLimit, task)) {
                    bpmTaskNewList.add(BpmTaskNew.build(task));
                }
            }
        }
        Page<BpmTaskNew> pageList = (Page) bpmTaskNewList;
        return new PageResult(pageList);*/
        List<BpmTaskNew> bpmTaskNewList = this.customBpmTaskManager.queryBpmTaskNewList(queryFilter);
        return new PageResult(bpmTaskNewList);
    }

    @RequestMapping("/bpm/task/getDelayTaskList")
    public PageResult getDelayTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = super.getQueryFilter(request);
        String sql = queryFilter.getFieldLogic().getWhereClauses().get(0).getSql();
        String materialNo = sql.replace("material_no in  (\"?id=", "").replace("\")", "");
        queryFilter.getFieldLogic().getWhereClauses().clear();
        queryFilter.addFilter("t.material_no", materialNo, QueryOP.EQUAL);
        List<TimeLimit> timeLimitList = timeLimitBpmTaskManager.getDelayTaskList(queryFilter);
        this.handleDelayTime(timeLimitList);
        Page<TimeLimit> pageList = (Page) timeLimitList;
        return new PageResult(pageList);
    }

    @RequestMapping("/bpm/task/queryDelayTasksGroupByMaterialNo")
    public PageResult queryDelayTasksGroupByMaterialNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = super.getQueryFilter(request);
        this.hanldeTimeCondition(queryFilter);
        List<DelayTaskCountDTO> delayTasks = timeLimitBpmTaskManager.queryDelayTasksGroupByMaterialNo(queryFilter);
        Page<TimeLimit> pageList = (Page) delayTasks;
        return new PageResult(pageList);
    }

    private void hanldeTimeCondition(QueryFilter queryFilter) {
        Iterator<WhereClause> iter = queryFilter.getFieldLogic().getWhereClauses().iterator();
        while (iter.hasNext()) {
            DefaultQueryField queryField = (DefaultQueryField) iter.next();
            if (ObjectUtils.nullSafeEquals("t.task_start_time", queryField.getField())) {
                queryFilter.addParamsFilter("taskStartTime", queryField.getValue());
                iter.remove();
            } else if (ObjectUtils.nullSafeEquals("t.task_end_time", queryField.getField())) {
                queryFilter.addParamsFilter("taskEndTime", queryField.getValue());
                iter.remove();
            }
        }
    }

    private void handleDelayTime(List<TimeLimit> timeLimitList) {
        if (CollectionUtils.isNotEmpty(timeLimitList)) {
            Iterator<TimeLimit> iterator = timeLimitList.iterator();
            while (iterator.hasNext()) {
                TimeLimit timeLimit = iterator.next();
                Date expectDealTime = DemoUtils.getExpectDealTime(timeLimit.getTaskStartTime(), timeLimit.getTimeLimit());
                Date compareDate = timeLimit.getTaskDealTime() == null ? new Date() : timeLimit.getTaskDealTime();
                timeLimit.setExpectDealTime(expectDealTime);
                timeLimit.setDelayFlag(expectDealTime.before(compareDate));
                timeLimit.setDelayTimePeriod(DemoUtils.calcDelayTimePeriod(expectDealTime, compareDate));
                /*if (timeLimit.getDelayFlag()) {
                    timeLimit.setDelayTimePeriod(DemoUtils.calcDelayTimePeriod(expectDealTime, compareDate));
                } else {
                // TODO 过滤掉未延期的任务(对分页会有影响)
                    iterator.remove();
                }*/
            }
            timeLimitList.sort((a, b) -> b.getIsDelay() - a.getIsDelay());
        }
    }

    @RequestMapping("/custom/my/todoTaskList")
    public PageResult todoTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = super.getQueryFilter(request);
        String userId = ContextUtil.getCurrentUserId();
        Set<String> userRights = this.taskIdentityLinkManager.getUserRights(userId);
        queryFilter.addParamsFilter("userRights", userRights);
        queryFilter.addParamsFilter("userId", ContextUtil.getCurrentUserId());
        List<BpmTaskNew> bpmTaskNewList = this.customBpmTaskManager.queryToDoList(queryFilter);
        /*List<BpmTask> listTodoTask = bpmTaskManager.getTodoList(ContextUtil.getCurrentUserId(), queryFilter);
        if (CollectionUtils.isNotEmpty(listTodoTask)) {
            List<TimeLimit> listTimeLimit = timeLimitBpmTaskManager.getTimeLimitList(listTodoTask);
            for (BpmTask task : listTodoTask) {
                if (!DemoUtils.addTaskNew(bpmTaskNewList, listTimeLimit, task)) {
                    bpmTaskNewList.add(BpmTaskNew.build(task));
                }
            }
        }
        Page<BpmTaskNew> pageList = (Page) bpmTaskNewList;*/
        return new PageResult(bpmTaskNewList);
    }

    @RequestMapping("/bpm/task/delayTask")
    public ResultMsg<String> delayTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 计算延期天数
        String taskEndTimeStr = RequestUtil.getRQString(request, "taskEndTime");
        String delayReason = RequestUtil.getRQString(request, "reason");
        // 延期后的处理时间
        Date taskEndTime = DateUtils.parseDate(taskEndTimeStr, "yyyy-MM-dd HH:mm:ss");
        TimeLimit timeLimit = this.timeLimitBpmTaskManager.getTimeLimitData(new TimeLimit(RequestUtil.getRQString(request, "taskId")));
//        Date expectDealTime = DemoUtils.getExpectDealTime(timeLimit.getTaskStartTime(), timeLimit.getTimeLimit());
        // 任务创建时间 与延期后的处理时间比较 计算出实际延期的天数
        String delayTime = DemoUtils.calcDelayTimePeriod(timeLimit.getTaskStartTime(), taskEndTime);
        timeLimit.setIsDelay(DemoConstants.DelayFlag.YES);
        timeLimit.setDelayTime(delayTime.substring(0, 1));
        timeLimit.setTaskEndTime(taskEndTime);
        timeLimit.setApplyUser(ContextUtil.getCurrentUserId());
        timeLimit.setDelayReason(delayReason);
        this.timeLimitBpmTaskManager.updateDelayTask(timeLimit);
        return getSuccessResult("延期任务成功");
    }

    @RequestMapping("/bpm/task/countTodoAndDelayTasks")
    public ResultMsg<TaskCountDTO> countTodoAndDelayTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        TaskCountDTO taskCount = this.customBpmTaskManager.countTodoAndDelayTasks(ContextUtil.getCurrentUserId());
        return this.getSuccessResult(taskCount);
    }

    @PostMapping("/bpm/task/exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response){
        Workbook workbook = null;
        try {
            String ids = request.getParameter("ids");
            QueryFilter queryFilter = new DefaultQueryFilter();
            queryFilter.addFilter("t.material_no", ids, QueryOP.IN);
            DefaultPage page = (DefaultPage) queryFilter.getPage();
            page.setLimit(10000);
            List<TimeLimit> data = this.timeLimitBpmTaskManager.getDelayTaskList(queryFilter);
            this.handleDelayTime(data);
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String sdf = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String fileName = URLEncoder.encode("任务延期统计表-" + sdf + ".xlsx", "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            ExportParams exportParams = new ExportParams("任务延期统计表", "sheet1");
            exportParams.setType(ExcelType.XSSF);
            exportParams.setTitleHeight((short) 20);
            workbook = ExcelExportUtil.exportExcel(exportParams, TimeLimit.class, data);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

}
