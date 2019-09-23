package com.dstz.demo.core.model;

import com.dstz.bpm.core.model.BpmTask;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * Created by lilx on 2019/9/22.
 */
public class BpmTaskNew extends BpmTask {

    protected boolean  delayFlag;

    protected Date expectDealTime;

    public static BpmTaskNew build(BpmTask task) {
        BpmTaskNew taskNew = new BpmTaskNew();
        BeanUtils.copyProperties(task, taskNew);
        return taskNew;
    }

    public Date getExpectDealTime() {
        return expectDealTime;
    }

    public void setExpectDealTime(Date expectDealTime) {
        this.expectDealTime = expectDealTime;
    }

    public boolean isDelayFlag() {
        return delayFlag;
    }

    public void setDelayFlag(boolean delayFlag) {
        this.delayFlag = delayFlag;
    }

}
