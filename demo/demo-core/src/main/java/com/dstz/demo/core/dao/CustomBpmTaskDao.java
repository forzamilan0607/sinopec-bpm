package com.dstz.demo.core.dao;

import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.dto.BpmTaskDTO;

import java.util.List;

public interface CustomBpmTaskDao extends BaseDao<String, BpmTaskDTO> {

    List<BpmTaskDTO> queryListByTaskIds(List<String> taskIds);
}
