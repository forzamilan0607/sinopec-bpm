package com.dstz.demo.core.dao;

import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.PurchasePlanHisRec;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface PurchasePlanHisRecManagerDao extends BaseDao<String, PurchasePlanHisRec> {
    
    void removeByMaterialProcessId(String materialProcessId);

    void insert(PurchasePlanHisRec purchasePlanHisRec);
}
