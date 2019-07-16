package com.zgs.modules.quartz.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.quartz.entity.QuartzJob;
import com.zgs.common.api.vo.Result;
import com.zgs.common.constant.CommonConstant;
import com.zgs.common.exception.ServiceException;
import com.zgs.common.util.oConvertUtils;
import com.zgs.modules.quartz.service.IQuartzJobService;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务在线管理
 * @author： zgs
 */
@RestController
@RequestMapping("/sys/quartzJob")
@Slf4j
public class QuartzJobController {

	@Autowired
	private IQuartzJobService quartzJobService;
	@Autowired
	private Scheduler scheduler;

	/**
	 * 分页列表查询
	 * 
	 * @param quartzJob
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result queryPageList(QuartzJob quartzJob,
								@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<QuartzJob> queryWrapper = new QueryWrapper<QuartzJob>(quartzJob);
		Page<QuartzJob> page = new Page<QuartzJob>(pageNo, pageSize);

		// 排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if ("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			} else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}

		IPage<QuartzJob> pageList = quartzJobService.page(page, queryWrapper);
		return Result.success(pageList);
	}

	/**
	 * 添加定时任务
	 * 
	 * @param quartzJob
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result add(@RequestBody QuartzJob quartzJob) {

		List<QuartzJob> list = quartzJobService.findByJobClassName(quartzJob.getJobClassName());
		if (list != null && list.size() > 0) {
			return Result.fail("该定时任务类名已存在");
		}

		try {

			schedulerAdd(quartzJob.getJobClassName().trim(), quartzJob.getCronExpression().trim(), quartzJob.getParameter());
			quartzJob.setDelFlag(CommonConstant.DEL_FLAG_0);
			quartzJob.setStatus(CommonConstant.STATUS_NORMAL);
			quartzJobService.save(quartzJob);
			return Result.success("创建定时任务成功");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}

	/**
	 * 更新定时任务
	 * 
	 * @param quartzJob
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result edit(@RequestBody QuartzJob quartzJob) {

		QuartzJob quartzJobEntity = quartzJobService.getById(quartzJob.getId());
		if (quartzJobEntity == null) {
			return Result.fail("未找到对应实体");
		} else {

			schedulerDelete(quartzJob.getJobClassName().trim());
			if (CommonConstant.STATUS_NORMAL == quartzJob.getStatus()) {
				schedulerAdd(quartzJob.getJobClassName().trim(), quartzJob.getCronExpression().trim(), quartzJob.getParameter());
			}
			boolean ok = quartzJobService.updateById(quartzJob);
			if (ok) {
				return Result.success("更新定时任务成功!");
			}
		}
		return Result.fail("跟新失败");
	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result delete(@RequestParam(name = "id", required = true) String id) {

		QuartzJob quartzJob = quartzJobService.getById(id);
		if (quartzJob == null) {
			return Result.fail("未找到对应实体");

		} else {
			QuartzJob job = quartzJobService.getById(id);
			schedulerDelete(job.getJobClassName().trim());
			boolean ok = quartzJobService.removeById(id);
			if (ok) {
				return Result.success("删除成功!");
			}
		}

		return Result.success("删除失败!");
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别！");

		} else {

			for (String id : Arrays.asList(ids.split(","))) {
				QuartzJob job = quartzJobService.getById(id);
				schedulerDelete(job.getJobClassName().trim());
				quartzJobService.removeById(id);
			}

			return Result.success("删除成功!");
		}
	}

	/**
	 * 暂停定时任务
	 * @param job
	 * @return
	 */
	@PostMapping(value = "/pause")
	@ApiOperation(value = "暂停定时任务")
	public Result pauseJob(@RequestBody QuartzJob job) {

		try {
			scheduler.pauseJob(JobKey.jobKey(job.getJobClassName().trim()));
		} catch (SchedulerException e) {
			throw new ServiceException("暂停定时任务失败");
		}
		job.setStatus(CommonConstant.STATUS_DISABLE);
		quartzJobService.updateById(job);
		return Result.success("暂停定时任务成功");
	}

	/**
	 * 启动定时任务
	 * @param job
	 * @return
	 */
	@PostMapping(value = "/resume")
	@ApiOperation(value = "恢复定时任务")
	public Result resumeJob(@RequestBody QuartzJob job) {

		try {
			scheduler.resumeJob(JobKey.jobKey(job.getJobClassName().trim()));
		} catch (SchedulerException e) {
			throw new ServiceException("恢复定时任务失败");
		}

		job.setStatus(CommonConstant.STATUS_NORMAL);
		quartzJobService.updateById(job);
		return Result.success("恢复定时任务成功");
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result queryById(@RequestParam(name = "id", required = true) String id) {

		QuartzJob quartzJob = quartzJobService.getById(id);
		if (quartzJob == null) {
			return Result.fail("未找到对应实体");

		} else {

			return Result.success(quartzJob);
		}
	}

	/**
	 * 添加定时任务
	 * 
	 * @param jobClassName
	 * @param cronExpression
	 * @param parameter
	 */
	private void schedulerAdd(String jobClassName, String cronExpression, String parameter) {

		try {
			// 启动调度器
			scheduler.start();

			// 构建job信息
			JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobClassName).usingJobData("parameter", parameter).build();

			// 表达式调度构建器(即任务执行的时间)
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

			// 按新的cronExpression表达式构建一个新的trigger
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName).withSchedule(scheduleBuilder).build();

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			log.error(e.toString());
			throw new ServiceException("创建定时任务失败");
		} catch (Exception e) {
			throw new ServiceException("后台找不到该类名任务");
		}
	}

	/**
	 * 删除定时任务
	 * 
	 * @param jobClassName
	 */
	private void schedulerDelete(String jobClassName) {

		try {
			scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName));
			scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName));
			scheduler.deleteJob(JobKey.jobKey(jobClassName));
		} catch (Exception e) {
			throw new ServiceException("删除定时任务失败");
		}
	}

	private static Job getClass(String classname) throws Exception {
		Class<?> class1 = Class.forName(classname);
		return (Job) class1.newInstance();
	}

}
