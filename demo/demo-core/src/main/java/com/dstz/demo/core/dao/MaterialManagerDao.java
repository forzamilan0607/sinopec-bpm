package com.dstz.demo.core.dao;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.dao.BaseDao;
import com.dstz.bpm.core.model.BpmInstance;
import com.dstz.demo.core.model.DemoSub;
import com.dstz.demo.core.model.MaterialProcess;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiangzhi.liu
 * @Date: 2019/11/2 18:43
 */
@MapperScan
public interface MaterialManagerDao extends BaseDao<String, MaterialProcess> {
    List<BpmInstance> instanceQuery(QueryFilter paramQueryFilter);

    Map<String,Object> getInstance(String purchaseAply);
}
