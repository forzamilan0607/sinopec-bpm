package com.dstz.demo.core.utils;

import com.dstz.bpm.core.model.BpmTask;
import com.dstz.demo.core.model.BpmTaskNew;
import com.dstz.demo.core.model.TimeLimit;

import java.util.Calendar;
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

    public static String calcDelayTimePeriod(Date startTime, Date endTime) {
        long nd = 1000 * 24 * 60 * 60;
        //一天的毫秒数
        long nh = 1000 * 60 * 60;
        //一小时的毫秒数
        long nm = 1000 * 60;
        //一分钟的毫秒数
        long ns = 1000;
        //一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = endTime.getTime() - startTime.getTime();
        long day = diff / nd;
        //计算差多少天
        long hour = diff % nd / nh;
        //计算差多少小时
        long min = diff % nd % nh / nm;
        //计算差多少分钟
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }

    public static Date getExpectDealTime(Date createTime, String timeLimitStr) {
        Date taskEndDate = null;
        if (timeLimitStr != null) {
            int day = timeLimitStr.contains("天") ? Integer.parseInt(timeLimitStr.substring(0, timeLimitStr.indexOf("天"))) : Integer.valueOf(timeLimitStr);
//            int hour =Integer.parseInt(timeLimitStr.substring(timeLimitStr.indexOf("天"),timeLimitStr.indexOf("时")));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTime);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);//让日期加1
            taskEndDate = calendar.getTime();
        }
        return taskEndDate;
    }
}
