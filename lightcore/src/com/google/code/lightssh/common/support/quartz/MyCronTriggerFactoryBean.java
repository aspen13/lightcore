package com.google.code.lightssh.common.support.quartz;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.scheduling.quartz.JobDetailAwareTrigger;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * 扩展CronTriggerFactoryBean
 * @author YangXiaojin
 *
 */
public class MyCronTriggerFactoryBean implements FactoryBean<CronTrigger>, BeanNameAware, InitializingBean {
	
	/** Constants for the CronTrigger class */
	private static final Constants constants = new Constants(CronTrigger.class);


	private String name;

	private String group;
	
	private JobDetail jobDetail;

	private JobDataMap jobDataMap = new JobDataMap();

	private Date startTime;

	private long startDelay;

	private String cronExpression;

	private TimeZone timeZone;

	private int priority;

	private int misfireInstruction;

	private String beanName;

	private CronTrigger cronTrigger;
	
	/**
	 * 描述
	 */
	protected String description;

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Specify the trigger's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Specify the trigger's group.
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Set the JobDetail that this trigger should be associated with.
	 */
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	/**
	 * Set the trigger's JobDataMap.
	 * @see #setJobDataAsMap
	 */
	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	/**
	 * Return the trigger's JobDataMap.
	 */
	public JobDataMap getJobDataMap() {
		return this.jobDataMap;
	}

	/**
	 * Register objects in the JobDataMap via a given Map.
	 * <p>These objects will be available to this Trigger only,
	 * in contrast to objects in the JobDetail's data map.
	 * @param jobDataAsMap Map with String keys and any objects as values
	 * (for example Spring-managed beans)
	 * @see org.springframework.scheduling.quartz.JobDetailBean#setJobDataAsMap
	 */
	public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
		this.jobDataMap.putAll(jobDataAsMap);
	}

	/**
	 * Set the start delay in milliseconds.
	 * <p>The start delay is added to the current system time (when the bean starts)
	 * to control the start time of the trigger.
	 */
	public void setStartDelay(long startDelay) {
		Assert.isTrue(startDelay >= 0, "Start delay cannot be negative");
		this.startDelay = startDelay;
	}

	/**
	 * Specify the cron expression for this trigger.
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * Specify the time zone for this trigger's cron expression.
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Specify the priority of this trigger.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Specify a misfire instruction for this trigger.
	 */
	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}

	/**
	 * Set the misfire instruction via the name of the corresponding
	 * constant in the {@link org.quartz.CronTrigger} class.
	 * Default is <code>MISFIRE_INSTRUCTION_SMART_POLICY</code>.
	 * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_FIRE_ONCE_NOW
	 * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING
	 * @see org.quartz.Trigger#MISFIRE_INSTRUCTION_SMART_POLICY
	 */
	public void setMisfireInstructionName(String constantName) {
		this.misfireInstruction = constants.asNumber(constantName).intValue();
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}


	public void afterPropertiesSet() {
		if (this.name == null) {
			this.name = this.beanName;
		}
		if (this.group == null) {
			this.group = Scheduler.DEFAULT_GROUP;
		}
		if (this.jobDetail != null) {
			this.jobDataMap.put(JobDetailAwareTrigger.JOB_DETAIL_KEY, this.jobDetail);
		}
		if (this.startDelay > 0) {
			this.startTime = new Date(System.currentTimeMillis() + this.startDelay);
		}
		else if (this.startTime == null) {
			this.startTime = new Date();
		}
		if (this.timeZone == null) {
			this.timeZone = TimeZone.getDefault();
		}

		Class<?> cronTriggerClass;
		Method jobKeyMethod;
		try {
			cronTriggerClass = getClass().getClassLoader().loadClass("org.quartz.impl.triggers.CronTriggerImpl");
			jobKeyMethod = JobDetail.class.getMethod("getKey");
		}
		catch (ClassNotFoundException ex) {
			cronTriggerClass = CronTrigger.class;
			jobKeyMethod = null;
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalStateException("Incompatible Quartz version");
		}
		BeanWrapper bw = new BeanWrapperImpl(cronTriggerClass);
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("name", this.name);
		pvs.add("group", this.group);
		if (jobKeyMethod != null) {
			pvs.add("jobKey", ReflectionUtils.invokeMethod(jobKeyMethod, this.jobDetail));
		}
		else {
			pvs.add("jobName", this.jobDetail.getKey().getName());
			pvs.add("jobGroup", this.jobDetail.getKey().getGroup());
		}
		pvs.add("jobDataMap", this.jobDataMap);
		pvs.add("startTime", this.startTime);
		pvs.add("cronExpression", this.cronExpression);
		pvs.add("timeZone", this.timeZone);
		pvs.add("priority", this.priority);
		pvs.add("misfireInstruction", this.misfireInstruction);
		pvs.add("description", this.description); //ADD this
		bw.setPropertyValues(pvs);
		this.cronTrigger = (CronTrigger) bw.getWrappedInstance();
	}


	public CronTrigger getObject() {
		return this.cronTrigger;
	}

	public Class<?> getObjectType() {
		return CronTrigger.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
