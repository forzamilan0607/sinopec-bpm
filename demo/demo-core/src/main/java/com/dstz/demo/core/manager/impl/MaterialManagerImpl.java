package com.dstz.demo.core.manager.impl;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.api.query.QueryOP;
import com.dstz.base.db.model.query.DefaultQueryFilter;
import com.dstz.base.manager.impl.BaseManager;
import com.dstz.bpm.core.manager.BpmDefinitionManager;
import com.dstz.bpm.core.model.BpmInstance;
import com.dstz.demo.core.dao.MaterialManagerDao;
import com.dstz.demo.core.manager.MaterialManager;
import com.dstz.demo.core.model.MaterialProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 16:57
 */
@Service("materialManager")
public class MaterialManagerImpl extends BaseManager<String, MaterialProcess> implements MaterialManager {
    @Resource
    MaterialManagerDao materialManagerDao;
    @Override
    public List<BpmInstance> instanceQuery(QueryFilter paramQueryFilter) {
        return materialManagerDao.instanceQuery(paramQueryFilter);
    }
    @Override
    public Map<String,Object> getInstance(String getPurchaseAply){
        return materialManagerDao.getInstance(getPurchaseAply);
    }
}
