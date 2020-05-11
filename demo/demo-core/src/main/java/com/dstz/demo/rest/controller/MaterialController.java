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
import com.dstz.base.db.model.query.DefaultPage;
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
import com.dstz.demo.core.model.PurchasePlanHisRec;
import com.dstz.demo.utils.EasyPoiUtil;
import com.dstz.form.api.model.FormType;
import com.dstz.org.api.model.IUser;
import com.dstz.sys.util.ContextUtil;
import com.dstz.sys.util.SysPropertyUtil;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
        String conditionSql = this.convertFilter(queryFilter);
        IUser user = ContextUtil.getCurrentUser();
        boolean isAdmin = ContextUtil.isAdmin(user);
        if (StringUtil.isNotEmpty(conditionSql)) {
            queryFilter.addParamsFilter("conditionParam", conditionSql);
        }
        if (!isAdmin) {
            queryFilter.addFilter("t.user_create", user.getUserId(), QueryOP.EQUAL);
            queryFilter.addParamsFilter("hisUpdateUser", user.getUserId());
            List<String> ids = this.materialManager.queryMaterialProcIdsByCurrentUser(user.getUserId());
            if (!CollectionUtils.isEmpty(ids)) {
                queryFilter.addParamsFilter("ids", ids);
            }
        }
        List<MaterialProcess> bpmDefinitionList = this.materialManager.query(queryFilter);
        for (MaterialProcess materialProcess : bpmDefinitionList) {
            if(StringUtils.isBlank(materialProcess.getProcessId())){
                materialProcess.setProcessId(this.extractProcessId());
            }
            // 不需要禁用checkbox 因为要做信息的补充
            /*if(materialProcess.isHasInst()){
                materialProcess.setDisabled(true);
            }*/
        }
        return new PageResult(bpmDefinitionList);
    }

    private String convertFilter(QueryFilter queryFilter) {
        String conditionSql = "";
        DefaultQueryField searchCondition = null;
        String value = "";
        Iterator iter = queryFilter.getFieldLogic().getWhereClauses().iterator();
        String startDate = null;
        String endDate = null;
        while (iter.hasNext()) {
            DefaultQueryField dqf = (DefaultQueryField) iter.next();
            if (ObjectUtils.nullSafeEquals(dqf.getField(), "id_")) {
                dqf.setField("id");
            }
            if (ObjectUtils.nullSafeEquals("searchValue", dqf.getField())) {
                value = dqf.getValue() + "";
                iter.remove();
            }
            if (ObjectUtils.nullSafeEquals("searchCondition", dqf.getField())) {
                dqf.setField(dqf.getValue() + "");
                searchCondition = dqf;
            }
            if (ObjectUtils.nullSafeEquals("gmt_create", dqf.getField())) {
                startDate = dqf.getValue() + "";
                iter.remove();
            }
            if (ObjectUtils.nullSafeEquals("gmt_create2", dqf.getField())) {
                endDate = dqf.getValue() + "";
                iter.remove();
            }
        }
        if (null != searchCondition) {
            searchCondition.setValue(value);
            conditionSql = CONDITION_MAP.get(searchCondition.getField()) + value + "\' ";
        }
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            conditionSql += "AND t.createTime >= '" + startDate + "' AND t.createTime <= '" + endDate + "'";
        } else if (StringUtils.isNotEmpty(startDate)) {
            conditionSql += "AND t.createTime >= '" + startDate + "'";
        } else if (StringUtils.isNotEmpty(endDate)) {
            conditionSql += "AND t.createTime <= '" + endDate + "'";
        }
        return conditionSql;
    }

    private static final Map<String, String> CONDITION_MAP = new ConcurrentHashMap();
    static {
        CONDITION_MAP.put("req_plan_no", "AND t.reqPlanNo=\'");
        CONDITION_MAP.put("reserved_number", "AND t.reservedNumber=\'");
        CONDITION_MAP.put("enquiry_name", "AND t.enquiryName=\'");
        CONDITION_MAP.put("purchase_order_no", "AND t.purchaseOrderNo=\'");
        CONDITION_MAP.put("erp_order_no", "AND t.erpOrderNo=\'");
        CONDITION_MAP.put("contract_no", "AND t.contractNo=\'");
        CONDITION_MAP.put("stock_voucher_no", "AND t.stockVoucherNo=\'");
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
            List<MaterialProcess> resultList = this.filterData(successList);
            List<String> materialNoList = resultList.stream().map(item -> item.getMaterialNo()).collect(Collectors.toList());
            QueryFilter queryFilter = new DefaultQueryFilter();
            com.dstz.base.api.Page page = new DefaultPage(1, materialNoList.size() * 3);
            queryFilter.setPage(page);
            queryFilter.addFilter("t.material_no", materialNoList, QueryOP.IN);
            List<MaterialProcess> existedData = this.materialManager.query(queryFilter);
            for (MaterialProcess material : resultList) {
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
                    material.setUpdateTime(new Date());
                    this.purchasePlanHisRecManager.update(material);
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

    private List<MaterialProcess> filterData(List<MaterialProcess> successList) {
        // 按物料ID分组
        Map<String, List<MaterialProcess>> groupMap = successList.stream().collect(Collectors.groupingBy(MaterialProcess::getMaterialNo));
        List<MaterialProcess> resultList = Lists.newArrayList();
        // 相同物料ID的，只取最后一条记录
        groupMap.forEach((k, v) -> {
            resultList.add(v.get(v.size() - 1));
        });
        return resultList;
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
//        String flowKey = SysPropertyUtil.getByAlias("materialPurchaseProcessKEY", "");
        String flowKey = this.getFlowKey();
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

    private String getFlowKey() {
        // 获取当前组织ID
        String groupId = ContextUtil.getCurrentGroupId();
        Map<String, String> map = JSONObject.parseObject(SysPropertyUtil.getByAlias("group-process"), Map.class);
        if (!CollectionUtils.isEmpty(map)) {
            if (map.containsKey(groupId)) {
                return map.get(groupId);
            }
        }
        return "goodsPurchasePlan";
    }

    private String extractProcessId() {
        // 获取当前组织ID
        String groupId = ContextUtil.getCurrentGroupId();
        Map<String, String> map = JSONObject.parseObject(SysPropertyUtil.getByAlias("group-processId"), Map.class);
        if (!CollectionUtils.isEmpty(map)) {
            if (map.containsKey(groupId)) {
                return map.get(groupId);
            }
        }
        return "411823371026956289";
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
        // 更新关联的采购计划历史记录
        Map<String, Object> params = new HashMap<>();
        params.put("materialId", material.getId());
        params.put("instId", bpmInstance.getId());
        this.purchasePlanHisRecManager.updateByInst(params);
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
        String flowKey = this.getFlowKey();
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
    public void exportData(HttpServletRequest request, HttpServletResponse response){
        Workbook workbook = null;
        try {
            String ids = request.getParameter("ids");
            QueryFilter queryFilter = new DefaultQueryFilter();
            queryFilter.addFilter("t.id", ids, QueryOP.IN);
            DefaultPage page = (DefaultPage) queryFilter.getPage();
            page.setLimit(ids.split(",").length);
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    @PostMapping({"queryPurchasePlanHis"})
    public PageResult queryPurchasePlanHis(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = this.getQueryFilter(request);
        String sql = queryFilter.getFieldLogic().getWhereClauses().get(0).getSql();
        String materialId = sql.replace("id_ in  (\"", "").replace("\")", "");
        List<PurchasePlanHisRec> data = this.purchasePlanHisRecManager.queryListByMaterialId(materialId);
        List<MaterialProcess> resultList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(data)) {
            int length = data.size();
            for (int i = 0; i < length; i++) {
                PurchasePlanHisRec item = data.get(i);
                MaterialProcess materialProcess = JSONObject.parseObject(item.getContent(), MaterialProcess.class);
                /*if (i + 1 == length) {
                    materialProcess.setCreateBy(item.getUpdateUser());
                } else {
                }*/
                materialProcess.setUpdateBy(item.getUpdateUser());
                resultList.add(materialProcess);
            }
        }
        return new PageResult(resultList);
    }
}
