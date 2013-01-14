package de.ronnyfriedland.time.logic.jobs;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;

class MockContext implements JobExecutionContext {

    private Date previousFireTime;

    @Override
    public Scheduler getScheduler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Trigger getTrigger() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Calendar getCalendar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isRecovering() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getRefireCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JobDataMap getMergedJobDataMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JobDetail getJobDetail() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Job getJobInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getFireTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getScheduledFireTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date date) {
        previousFireTime = date;
    }

    @Override
    public Date getNextFireTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFireInstanceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setResult(Object result) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getJobRunTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void put(Object key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object get(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

}