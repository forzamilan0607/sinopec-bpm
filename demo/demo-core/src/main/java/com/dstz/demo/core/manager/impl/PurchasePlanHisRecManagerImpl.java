package com.dstz.demo.core.manager.impl;

import com.alibaba.fastjson.JSONObject;
import com.dstz.demo.core.dao.PurchasePlanHisRecManagerDao;
import com.dstz.demo.core.manager.PurchasePlanHisRecManager;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.core.model.PurchasePlanHisRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("purchasePlanHisRecManager")
public class PurchasePlanHisRecManagerImpl implements PurchasePlanHisRecManager {

    @Autowired
    private PurchasePlanHisRecManagerDao purchasePlanHisRecManagerDao;

    @Override
    public void removeByMaterialProcessId(String materialProcessId) {
        this.purchasePlanHisRecManagerDao.removeByMaterialProcessId(materialProcessId);
    }

    @Override
    public void update(MaterialProcess material) {
        PurchasePlanHisRec purchasePlanHisRec = new PurchasePlanHisRec();
        purchasePlanHisRec.setMaterialProcessId(material.getId());
        purchasePlanHisRec.setContent(JSONObject.toJSONString(material));
        purchasePlanHisRec.setUpdateUser(material.getUpdateBy());
        this.purchasePlanHisRecManagerDao.insert(purchasePlanHisRec);
    }
}
