package com.dstz.demo.core.manager;

import com.dstz.base.manager.Manager;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.core.model.PurchasePlanHisRec;

/**
 * 采购计划历史记录manager
 * @author chris
 * @since Jan 17.20
 */
public interface PurchasePlanHisRecManager extends Manager<String, PurchasePlanHisRec> {

    void removeByMaterialProcessId(String materialProcessId);

    void update(MaterialProcess material);
}
