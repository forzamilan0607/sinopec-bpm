package com.dstz.demo.core.model.dto;

import java.io.Serializable;
import java.util.List;

public class BpmTaskBatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String opinion;

    private String action;

    private List<BpmTaskDTO> taskList;

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public List<BpmTaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<BpmTaskDTO> taskList) {
        this.taskList = taskList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
