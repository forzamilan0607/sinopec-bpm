package com.dstz.demo.core.manager;

import com.dstz.demo.core.model.MaterialProcess;

/**
 * 采购计划历史记录manager
 * @author chris
 * @since Jan 17.20
 */
public interface PurchasePlanHisRecManager{

    void removeByMaterialProcessId(String materialProcessId);

    void update(MaterialProcess material);
}
