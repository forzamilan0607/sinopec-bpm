package com.dstz.demo.core.dao;

import com.dstz.base.api.query.QueryFilter;
import com.dstz.base.dao.BaseDao;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.dto.BpmTaskDTO;
import com.dstz.demo.core.model.dto.TaskCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomBpmTaskDao extends BaseDao<String, BpmTaskDTO> {

    List<BpmTaskDTO> queryListByTaskIds(@Param("taskIds") List<String> taskIds);

    List<String> queryTaskNames(@Param("userId") String userId);

    TaskCountDTO countTodoAndDelayTasks(@Param("userId") String userId);

    List<BpmTaskNew> queryBpmTaskNewList(QueryFilter queryFilter);

    List<BpmTaskNew> queryToDoList(QueryFilter queryFilter);
}
