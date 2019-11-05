package com.dstz.demo.rest.controller;

import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.exception.BusinessException;
import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.core.id.IdUtil;
import com.dstz.base.core.util.StringUtil;
import com.dstz.base.db.model.page.PageResult;
import com.dstz.base.rest.ControllerTools;
import com.dstz.base.rest.util.RequestUtil;
import com.dstz.bpm.api.engine.action.cmd.FlowRequestParam;
import com.dstz.bpm.api.engine.data.BpmFlowDataAccessor;
import com.dstz.bpm.api.engine.data.result.BpmFlowInstanceData;
import com.dstz.bpm.api.engine.data.result.FlowData;
import com.dstz.bpm.api.exception.BpmStatusCode;
import com.dstz.bpm.core.manager.BpmDefinitionManager;
import com.dstz.bpm.core.manager.BpmInstanceManager;
import com.dstz.bpm.core.model.BpmDefinition;
import com.dstz.bpm.core.model.BpmInstance;
import com.dstz.bpm.engine.action.cmd.DefaultInstanceActionCmd;
import com.dstz.demo.core.manager.MaterialManager;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.utils.EasyPoiUtil;
import com.dstz.form.api.model.FormType;
import com.dstz.sys.util.SysPropertyUtil;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物料采购流程
 * @Author: lxzhi
 * @Date: 2019/10/31 15:11
 */
@Slf4j
@RestController
@RequestMapping("/bpm/material/process")
public class MaterialController extends ControllerTools {
    @Resource
    private BpmDefinitionManager bpmDefinitionManager;
    @Resource
    private MaterialManager materialManager;
    @Resource
    BpmFlowDataAccessor bpmFlowDataAccessor;
    @Resource
    BpmDefinitionManager bpmDefinitionMananger;
    @Resource
    BpmInstanceManager bpmInstanceManager;

    @PostMapping({"listJson"})
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = this.getQueryFilter(request);
        List<MaterialProcess> bpmDefinitionList = this.materialManager.query(queryFilter);
        return new PageResult(bpmDefinitionList);
    }
    /**
     * @Description: 
     * @Param: 导入
     * @Return: com.dstz.base.api.response.impl.ResultMsg<com.alibaba.fastjson.JSONObject>
     * @Author: xiangzhi.liu
     * @Date: 2019/11/1 16:20
     */
    @PostMapping("/import")
    public ResultMsg<String> importExcel(MultipartFile file,
                                             HttpServletRequest ret ,
                                             HttpServletResponse rsp){
        ExcelImportResult<MaterialProcess> result = EasyPoiUtil.importExcel(file,2,1,true,MaterialProcess.class);
        List<MaterialProcess> successList = result.getList();
        List<MaterialProcess> failList = result.getFailList();
        log.info("是否存在验证未通过的数据:" + result.isVerfiyFail());
        log.info("验证通过的数量:" + successList.size());
        log.info("验证未通过的数量:" + failList.size());
        if(result.isVerfiyFail()){
            log.error("未通过数据：{}",failList);
            ret.getSession().setAttribute("materialList", failList);
            RequestDispatcher requestDispatcher = ret.getRequestDispatcher("/bpm/material/process/down/errorExcel" );
            try {
                rsp.setStatus(302);
//                rsp.sendRedirect(ret.getContextPath()+"/bpm/material/process/down/errorExcel");
//                requestDispatcher.forward(ret, rsp);
            }catch (Exception e){
                e.printStackTrace();
            }
            return super.getSuccessResult(ret.getContextPath()+"/bpm/material/process/down/errorExcel");
        }
        for (MaterialProcess material : successList) {
            material.setId(IdUtil.getSuid());
            materialManager.create(material);
        }
        return super.getSuccessResult("导入成功");
    }

    @RequestMapping("/getInstanceData")
    public ResultMsg<FlowData> getInstanceData(HttpServletRequest request) {
        String materialId = request.getParameter("materialId");
        String instanceId = request.getParameter("instanceId");
        Boolean readonly = RequestUtil.getBoolean(request, "readonly", false);
        String flowKey = SysPropertyUtil.getByAlias("materialPurchaseProcessKEY", "");
        String nodeId = RequestUtil.getString(request, "nodeId");
        BpmDefinition def = this.bpmDefinitionMananger.getByKey(flowKey);
        if (def == null) {
            throw new BusinessException("流程定义查找失败！ flowKey： " + flowKey, BpmStatusCode.DEF_LOST);
        }
        String defId = def.getId();
        String formType = RequestUtil.getString(request, "formType", FormType.PC.value());
        Map initDataMap = new HashMap<>();
        MaterialProcess material = this.materialManager.get(materialId);
        material.setProcessId(defId);
        initDataMap.put("processMaterial",JSONObject.toJSON(material));
        if (StringUtil.isNotEmpty(nodeId)) {
            BpmFlowInstanceData instanceData = this.bpmFlowDataAccessor.getInstanceData(instanceId, FormType.fromValue(formType), nodeId);
            return this.getSuccessResult(instanceData);
        } else {
            FlowData data = this.bpmFlowDataAccessor.getStartFlowData(defId, instanceId, FormType.fromValue(formType), readonly);
            BpmFlowInstanceData bpmFlowInstanceData=(BpmFlowInstanceData)data;
            JSONObject json = new JSONObject();
            json.putAll(initDataMap);
            bpmFlowInstanceData.setData(json);
            return this.getSuccessResult(data);
        }
    }

    /**
     * @Description: 
     * @Param: 启动流程
     * @Return: com.dstz.base.api.response.impl.ResultMsg<com.alibaba.fastjson.JSONObject>
     * @Author: xiangzhi.liu
     * @Date: 2019/11/1 16:21
     */
    @PostMapping("/start")
    public ResultMsg<String> startProcess(@RequestBody FlowRequestParam flowParam){
        DefaultInstanceActionCmd instanceCmd = new DefaultInstanceActionCmd(flowParam);
        String businessKey = flowParam.getBusinessKey();
        flowParam.setBusinessKey(null);
        String actionName = instanceCmd.executeCmd();
        MaterialProcess material = this.materialManager.get(businessKey);
        this.materialManager.remove(businessKey);
        BpmInstance bpmInstance = bpmInstanceManager.get(instanceCmd.getInstanceId());
        BpmDefinition def = this.bpmDefinitionMananger.getByKey(bpmInstance.getDefKey());
        String defName = StringUtils.replace(bpmInstance.getDefName(),def.getName(),material.getEnquiryName());
        bpmInstance.setDefName(defName);
        String subject = StringUtils.replace(bpmInstance.getSubject(),def.getName(),material.getEnquiryName());
        bpmInstance.setSubject(subject);
        bpmInstanceManager.update(bpmInstance);
        return this.getSuccessResult(instanceCmd.getInstanceId(), actionName + "成功");
    }

    @PostMapping({"instance/listJson"})
    public PageResult instanceIstJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = this.getQueryFilter(request);
        Page<BpmInstance> bpmInstanceList = (Page)this.materialManager.instanceQuery(queryFilter);
        return new PageResult(bpmInstanceList);
    }

    @RequestMapping("/down/errorExcel")
    public void getInstanceData(HttpServletRequest ret,HttpServletResponse resp) {
        List<MaterialProcess> failList = (List<MaterialProcess>) ret.getSession().getAttribute("materialList");
        EasyPoiUtil.exportExcel(failList,"未通过数据",MaterialProcess.class,"验证未通过数据.xslx",resp);
    }
}
