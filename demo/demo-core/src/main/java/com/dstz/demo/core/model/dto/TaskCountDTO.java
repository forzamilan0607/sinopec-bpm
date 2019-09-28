package com.dstz.demo.core.model.dto;

import java.io.Serializable;

public class TaskCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int todoTaskCount;

    private int delayTaskCount;

    public int getTodoTaskCount() {
        return todoTaskCount;
    }

    public void setTodoTaskCount(int todoTaskCount) {
        this.todoTaskCount = todoTaskCount;
    }

    public int getDelayTaskCount() {
        return delayTaskCount;
    }

    public void setDelayTaskCount(int delayTaskCount) {
        this.delayTaskCount = delayTaskCount;
    }
}
