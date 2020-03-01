package com.dstz.demo.core.manager;

import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.core.model.PurchasePlanHisRec;

import java.util.List;
import java.util.Map;

/**
 * 采购计划历史记录manager
 * @author chris
 * @since Jan 17.20
 */
public interface PurchasePlanHisRecManager{

    void removeByMaterialProcessId(String materialProcessId);

    void update(MaterialProcess material);

    List<PurchasePlanHisRec> queryListByMaterialId(String materialId);

    void updateByInst(Map<String, Object> params);
}
