package com.dstz.demo.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.rest.ControllerTools;
import com.dstz.bpm.api.engine.action.cmd.FlowRequestParam;
import com.dstz.bpm.engine.action.cmd.DefualtTaskActionCmd;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.model.dto.BpmTaskBatchDTO;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.sys.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
public class CustomBpmTaskController extends ControllerTools {
    private Logger logger = LoggerFactory.getLogger(CustomBpmTaskController.class);

    @Autowired
    private CustomBpmTaskManager customBpmTaskManager;

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
}
