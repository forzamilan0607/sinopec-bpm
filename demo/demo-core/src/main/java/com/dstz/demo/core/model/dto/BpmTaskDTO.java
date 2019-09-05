package com.dstz.demo.core.model.dto;

import java.io.Serializable;

public class BpmTaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String taskName;
    private String instId;
    private String defId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }
}
