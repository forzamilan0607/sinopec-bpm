package com.dstz.demo.core.dao;

import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomBpmTaskDao extends BaseDao<String, BpmTaskDTO> {

    List<BpmTaskDTO> queryListByTaskIds(@Param("taskIds") List<String> taskIds);

    List<String> queryTaskNames(@Param("userId") String userId);
}
