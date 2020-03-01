package com.dstz.demo.core.dao;

import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.PurchasePlanHisRec;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.Map;

@MapperScan
public interface PurchasePlanHisRecDao extends BaseDao<String, PurchasePlanHisRec> {
    
    void removeByMaterialProcessId(String materialProcessId);

    void insert(PurchasePlanHisRec purchasePlanHisRec);

    List<PurchasePlanHisRec> queryListByMaterialId(String materialId);

    void updateByInst(Map<String, Object> params);
}
