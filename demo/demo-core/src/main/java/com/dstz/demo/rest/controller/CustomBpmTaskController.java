package com.dstz.demo.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.rest.ControllerTools;
import com.dstz.bpm.api.engine.data.BpmFlowDataAccessor;
import com.dstz.bpm.api.engine.data.result.BpmFlowData;
import com.dstz.form.api.model.FormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomBpmTaskController extends ControllerTools {

    @Autowired
    private BpmFlowDataAccessor bpmFlowDataAccessor;

    @RequestMapping("/bpm/task/getMultiTaskData")
    public ResultMsg<JSONObject> getTaskData(@RequestBody List<String> taskIds){
        List<BpmFlowData> flowDataList = taskIds.stream().parallel().map(taskId ->
                this.bpmFlowDataAccessor.getFlowTaskData(taskId, FormType.fromValue(FormType.PC.value()))
        ).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", flowDataList);
        return super.getSuccessResult(jsonObject);
    }
}
