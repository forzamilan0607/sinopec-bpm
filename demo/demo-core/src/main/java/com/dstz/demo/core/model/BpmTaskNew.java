package com.dstz.demo.core.model;

import com.dstz.bpm.core.model.BpmTask;
import org.springframework.beans.BeanUtils;

/**
 * Created by lilx on 2019/9/22.
 */
public class BpmTaskNew extends BpmTask {

    protected boolean  delayFlag;

    public static BpmTaskNew build(BpmTask task) {
        BpmTaskNew taskNew = new BpmTaskNew();
        BeanUtils.copyProperties(task, taskNew);
        return taskNew;
    }

    public boolean isDelayFlag() {
        return delayFlag;
    }

    public void setDelayFlag(boolean delayFlag) {
        this.delayFlag = delayFlag;
    }

}
