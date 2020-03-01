package com.dstz.demo.core.manager.impl;

import com.alibaba.fastjson.JSONObject;
import com.dstz.demo.core.dao.PurchasePlanHisRecDao;
import com.dstz.demo.core.manager.PurchasePlanHisRecManager;
import com.dstz.demo.core.model.MaterialProcess;
import com.dstz.demo.core.model.PurchasePlanHisRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("purchasePlanHisRecManager")
public class PurchasePlanHisRecManagerImpl implements PurchasePlanHisRecManager {

    @Autowired
    private PurchasePlanHisRecDao purchasePlanHisRecDao;

    @Override
    public void removeByMaterialProcessId(String materialProcessId) {
        this.purchasePlanHisRecDao.removeByMaterialProcessId(materialProcessId);
    }

    @Override
    public void update(MaterialProcess material) {
        PurchasePlanHisRec purchasePlanHisRec = new PurchasePlanHisRec();
        purchasePlanHisRec.setMaterialProcessId(material.getId());
        purchasePlanHisRec.setContent(JSONObject.toJSONString(material));
        purchasePlanHisRec.setUpdateUser(material.getUpdateBy());
        this.purchasePlanHisRecDao.insert(purchasePlanHisRec);
    }

    @Override
    public List<PurchasePlanHisRec> queryListByMaterialId(String materialId) {
        return this.purchasePlanHisRecDao.queryListByMaterialId(materialId);
    }

    @Override
    public void updateByInst(Map<String, Object> params) {
        this.purchasePlanHisRecDao.updateByInst(params);
    }
}
