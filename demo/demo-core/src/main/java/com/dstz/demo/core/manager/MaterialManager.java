package com.dstz.demo.core.manager;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.manager.Manager;

import com.dstz.bpm.core.model.BpmInstance;
import com.dstz.demo.core.model.MaterialProcess;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 16:39
 */
public interface MaterialManager extends Manager<String, MaterialProcess> {
    List<BpmInstance> instanceQuery(QueryFilter paramQueryFilter);

    Map<String,Object> getInstance(MaterialProcess materialProcess);
}
