package com.dstz.demo.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.rest.ControllerTools;
import com.dstz.demo.core.manager.CustomBpmTaskManager;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
public class CustomBpmTaskController extends ControllerTools {

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

    private List<String> extractTaskIdsFromRequest(HttpServletRequest request) {
        String taskIds = request.getParameter("taskIds");
        return Arrays.asList(taskIds.split(","));
    }
}
