package com.dstz.demo.rest.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dstz.base.api.exception.BusinessException;
import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.query.QueryOP;
import com.dstz.base.api.response.impl.ResultMsg;
import com.dstz.base.core.id.IdUtil;
import com.dstz.base.core.util.StringUtil;
import com.dstz.base.db.model.page.PageResult;
import com.dstz.base.db.model.query.DefaultQueryField;
import com.dstz.base.db.model.query.DefaultQueryFilter;
import com.dstz.base.rest.ControllerTools;
import com.dstz.base.rest.util.RequestUtil;
import com.dstz.bpm.api.engine.action.cmd.FlowRequestParam;
import com.dstz.bpm.api.engine.data.BpmFlowDataAccessor;
import com.dstz.bpm.api.engine.data.result.BpmFlowInstanceData;
import com.dstz.bpm.api.engine.data.result.FlowData;
import com.dstz.bpm.api.exception.BpmStatusCode;
import com.dstz.bpm.core.manager.BpmDefinitionManager;
import com.dstz.bpm.core.manager.BpmInstanceManager;
import com.dstz.bpm.core.manager.BpmTaskManager;
import com.dstz.bpm.core.model.BpmDefinition;
import com.dstz.bpm.core.model.BpmInstance;
import com.dstz.bpm.core.model.BpmTask;
import com.dstz.bpm.engine.action.cmd.DefaultInstanceActionCmd;
import com.dstz.demo.core.manager.MaterialManager;
import com.dstz.demo.core.manager.PurchasePlanHisRecManager;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.utils.EasyPoiUtil;
import com.dstz.form.api.model.FormType;
import com.dstz.org.api.model.IUser;
import com.dstz.sys.util.ContextUtil;
import com.dstz.sys.util.SysPropertyUtil;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Resource
    BpmTaskManager bpmTaskManager;

    @Resource
    private PurchasePlanHisRecManager purchasePlanHisRecManager;

    @PostMapping({"listJson"})
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = this.getQueryFilter(request);
        IUser user = ContextUtil.getCurrentUser();
        boolean isAdmin = ContextUtil.isAdmin(user);
        if (!isAdmin) {
            queryFilter.addFilter("t.user_create", user.getUserId(), QueryOP.EQUAL);
            List<String> ids = this.materialManager.queryMaterialProcIdsByCurrentUser(user.getUserId());
            queryFilter.addParamsFilter("hisUpdateUser", user.getUserId());
            if (!CollectionUtils.isEmpty(ids)) {
                queryFilter.addParamsFilter("ids", ids);
            }
        }
        if (null != queryFilter.getFieldLogic() && !CollectionUtils.isEmpty(queryFilter.getFieldLogic().getWhereClauses())) {
            queryFilter.getFieldLogic().getWhereClauses().forEach(item -> {
                DefaultQueryField dqf = (DefaultQueryField) item;
                if (ObjectUtils.nullSafeEquals(dqf.getField(), "id_")) {
                    dqf.setField("id");
                }
            });
        }
        List<MaterialProcess> bpmDefinitionList = this.materialManager.query(queryFilter);
        for (MaterialProcess materialProcess : bpmDefinitionList) {
            if(StringUtils.isBlank(materialProcess.getProcessId())){
                materialProcess.setProcessId("411823371026956289");
            }
            // 不需要禁用checkbox 因为要做信息的补充
            /*if(materialProcess.isHasInst()){
                materialProcess.setDisabled(true);
            }*/
        }
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
    public ResultMsg<JSONObject> importExcel(MultipartFile file, HttpServletRequest ret , HttpServletResponse rsp){
        JSONObject json = new JSONObject();
        json.put("isSuccess",true);
        ExcelImportResult<MaterialProcess> result = EasyPoiUtil.importExcel(file,1,1,true,MaterialProcess.class);
        List<MaterialProcess> successList = result.getList();
        List<MaterialProcess> failList = result.getFailList();
        log.info("是否存在验证未通过的数据:" + result.isVerfiyFail() + ", 验证通过的数量:" + successList.size() + "验证未通过的数量:" + failList.size());
        StringBuilder errorMsg = new StringBuilder();
        IUser currentUser = ContextUtil.getCurrentUser();
        //保存成功信息
        if (!CollectionUtils.isEmpty(successList)) {
            QueryFilter queryFilter = new DefaultQueryFilter();
            queryFilter.addFilter("t.material_no", successList.stream().map(item -> item.getMaterialNo()).collect(Collectors.toList()), QueryOP.IN);
            List<MaterialProcess> existedData = this.materialManager.query(queryFilter);
            for (MaterialProcess material : successList) {
                // 判断是否存在
                MaterialProcess item = this.findItem(material, existedData);
                if (null != item) {
//                if(existedData != null && existedData.get("id") != null){
                    /*if("true".equalsIgnoreCase((String)existedData.get("isInstance"))){
                        if (errorCount++ == 0) {
                            errorMsg.append("采购申请编号：");
                        }
                        errorMsg.append("【"+material.getPurchaseAply()+"】");
                        continue;
                    }*/
                    material.setId(item.getId());
                    material.setUpdateBy(currentUser.getUserId());
                    materialManager.update(material);
                    // 同时更新采购计划历史记录
                    this.purchasePlanHisRecManager.update(material);
                } else {
                    material.setId(IdUtil.getSuid());
                    material.setCreateBy(currentUser.getUserId());
                    materialManager.create(material);
                }
            }
        }
        if(result.isVerfiyFail()){
            log.error("未通过数据：{}",failList);
            ret.getSession().setAttribute("materialList", failList);
            rsp.setStatus(302);
            json.put("isSuccess",false);
            json.put("successCount", successList.size());
            json.put("failedCount", failList.size());
            json.put("url",ret.getContextPath()+"/bpm/material/process/down/errorExcel");
            return super.getSuccessResult(json);
        }
        if(errorMsg.length() > 0){
            errorMsg.append("已启动对应流程！");
            json.put("isSuccess",false);
            json.put("validateList", errorMsg);
        }
        return super.getSuccessResult(json);
    }

    private MaterialProcess findItem(MaterialProcess material, List<MaterialProcess> existedData) {
        if (!CollectionUtils.isEmpty(existedData)) {
            for (MaterialProcess item : existedData) {
                if (ObjectUtils.nullSafeEquals(material.getMaterialNo(), item.getMaterialNo())) {
                    return item;
                }
            }
        }
        return null;
    }

    @RequestMapping("/getInstanceData")
    public ResultMsg<FlowData> getInstanceData(HttpServletRequest request) {
        String materialId = request.getParameter("materialId");
        if(StringUtils.isBlank(materialId)){
            materialId = (String)request.getAttribute("materialId");
        }
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
        MaterialProcess material = this.materialManager.get(flowParam.getBusinessKey());
        Map<String,Object> tmp = this.materialManager.getInstance(new MaterialProcess(material.getMaterialNo(), material.getPurchaseAply()));
        if ("true".equalsIgnoreCase((String) tmp.get("isInstance"))) {
            return this.getSuccessResult("当前流程已经启动");
        }
        DefaultInstanceActionCmd instanceCmd = new DefaultInstanceActionCmd(flowParam);
        String businessKey = flowParam.getBusinessKey();
        flowParam.setBusinessKey(null);
        String actionName = instanceCmd.executeCmd();
        this.materialManager.remove(businessKey);
        BpmInstance bpmInstance = bpmInstanceManager.get(instanceCmd.getInstanceId());
        BpmDefinition def = this.bpmDefinitionMananger.getByKey(bpmInstance.getDefKey());
        String defName = StringUtils.replace(bpmInstance.getDefName(),def.getName(),material.getMaterialDesc());
        bpmInstance.setDefName(defName);
        String subject = StringUtils.replace(bpmInstance.getSubject(),def.getName(),material.getMaterialDesc());
        bpmInstance.setSubject(subject);
        bpmInstanceManager.update(bpmInstance);
        List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(instanceCmd.getInstanceId());
        if(bpmTaskList!=null && !bpmTaskList.isEmpty()){
            for (BpmTask bpmTask : bpmTaskList) {
                String _tmp = StringUtils.replace(bpmTask.getSubject(),def.getName(),material.getMaterialDesc());
                bpmTask.setSubject(_tmp);
                bpmTaskManager.update(bpmTask);
            }
        }
        return this.getSuccessResult(instanceCmd.getInstanceId(), actionName + "成功");
    }
    @PostMapping("/start/batch")
    public ResultMsg<String> batchStartProcess(@RequestBody List<MaterialProcess> materialList,HttpServletRequest request){
        FlowRequestParam flowParam = new FlowRequestParam();
        flowParam.setAction("start");
        flowParam.setFormType("INNER");
        flowParam.setInstanceId("");
        JSONObject json = new JSONObject();
        String flowKey = SysPropertyUtil.getByAlias("materialPurchaseProcessKEY", "");
        BpmDefinition def = this.bpmDefinitionMananger.getByKey(flowKey);
        if (def == null) {
            throw new BusinessException("流程定义查找失败！ flowKey： " + flowKey, BpmStatusCode.DEF_LOST);
        }
        String defId = def.getId();
        for (MaterialProcess material : materialList) {
            material.setProcessId(defId);
            flowParam.setBusinessKey(material.getId());
            String materialJson = JSON.toJSONStringWithDateFormat(material,"yyyy-MM-dd HH:mm:ss");
            json.put("processMaterial",materialJson);
            flowParam.setData(json);
            flowParam.setDefId(defId);
            this.startProcess(flowParam);
        }
        return this.getSuccessResult("批量启动成功");
    }


    /**
     * 批量补充物料采购计划信息
     * @param materialList
     * @return
     */
    @PostMapping("/batchSupplement")
    public ResultMsg<String> batchSupplement(@RequestBody List<MaterialProcess> materialList){
        materialList.forEach(item -> {
            item.setUpdateBy(ContextUtil.getCurrentUserId());
            this.materialManager.update(item);
            this.purchasePlanHisRecManager.update(item);
        });
        return this.getSuccessResult("保存成功");
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
        EasyPoiUtil.exportExcel(failList,"导入失败说明",MaterialProcess.class,"导入失败说明",resp);
    }

    @RequestMapping("/deletePurchasePlan")
    public ResultMsg<String> deletePurchasePlan(HttpServletRequest request){
        String id = request.getParameter("id");
        LOG.error("id = {}", id);
        this.materialManager.remove(id);
        this.purchasePlanHisRecManager.removeByMaterialProcessId(id);
        return super.getSuccessResult("删除成功");
    }

    @PostMapping("/exportData")
    public ResultMsg<String> exportData(HttpServletRequest request, HttpServletResponse response){
        Workbook workbook = null;
        try {
            String ids = request.getParameter("ids");
            QueryFilter queryFilter = new DefaultQueryFilter();
            queryFilter.addFilter("t.id", ids, QueryOP.IN);
            List<MaterialProcess> materialList = this.materialManager.query(queryFilter);
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String sdf = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String fileName = URLEncoder.encode("物料采购计划表-" + sdf + ".xlsx", "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            ExportParams exportParams = new ExportParams("物料采购计划表", "sheet1");
            exportParams.setType(ExcelType.XSSF);
            exportParams.setTitleHeight((short) 20);
            workbook = ExcelExportUtil.exportExcel(exportParams, MaterialProcess.class, materialList);
            workbook.write(response.getOutputStream());
            return this.getSuccessResult("导出成功");
        } catch (IOException e) {
            e.printStackTrace();
            ResultMsg<String> resultMsg = new ResultMsg<String>();
            resultMsg.setOk(false);
            resultMsg.setMsg("导出失败");
            return resultMsg;
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }
}
