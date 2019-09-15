package com.dstz.demo.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.rest.ControllerTools;
import com.dstz.bpm.api.engine.action.button.ButtonChecker;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.model.dto.BpmTaskBatchDTO;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        logger.error("params = " + param);
        /*taskModel = new DefualtTaskActionCmd(params);
        String actionName = taskModel.executeCmd();
        return this.getSuccessResult(String.format("执行%s操作成功", actionName));*/
        return this.getSuccessResult("OKOK");
    }

    @RequestMapping("/bpm/instance/doMultiAction")
    public ResultMsg<String> doAction4Instances(@RequestBody BpmTaskBatchDTO param) throws Exception {
        logger.error("params = " + param);
        return this.getSuccessResult("OKOK");
    }

    private List<String> extractTaskIdsFromRequest(HttpServletRequest request) {
        String taskIds = request.getParameter("taskIds");
        return Arrays.asList(taskIds.split(","));
    }
}
