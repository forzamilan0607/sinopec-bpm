package com.dstz.demo.core.utils;

import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.TimeLimit;

import java.util.Date;
import java.util.List;

public final class DemoUtils {

    public static boolean addTaskNew(List<BpmTaskNew> bpmTaskNewList, List<TimeLimit> listTimeLimit, BpmTask task) {
        for (TimeLimit timeLimit : listTimeLimit) {
            if (task.getId().equals(timeLimit.getTaskId())) {
                BpmTaskNew taskNew = BpmTaskNew.build(task);
                taskNew.setDelayFlag(timeLimit.getTaskEndTime().before(new Date()));
                taskNew.setExpectDealTime(timeLimit.getTaskEndTime());
                bpmTaskNewList.add(taskNew);
                return true;
            }
        }
        return false;
    }
}
