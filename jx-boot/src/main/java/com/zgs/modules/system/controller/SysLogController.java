package com.zgs.modules.system.controller;


import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.system.entity.SysLog;
import com.zgs.modules.system.service.ISysLogService;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志表 前端控制器
 *
 * @author zgs
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {
	
	@Autowired
	private ISysLogService sysLogService;
	
	/**
	 * 查询日志记录
	 * @param syslog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result queryPageList(SysLog syslog,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<SysLog> queryWrapper = new QueryWrapper<SysLog>(syslog);
		Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);

		//开始结束时间
		String beginTime = req.getParameter("createTime_begin");
		String endTime = req.getParameter("createTime_end");
		if(oConvertUtils.isNotEmpty(beginTime) && oConvertUtils.isNotEmpty(endTime)) {
			queryWrapper.ge(oConvertUtils.camelToUnderline("createTime"), beginTime);
			queryWrapper.le(oConvertUtils.camelToUnderline("createTime"), endTime);
		}

		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			}else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}

		//日志关键词
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}

		IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
		return Result.success(pageList);
	}
	
	/**
	 * 删除单个日志记录
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result delete(@RequestParam(name="id") String id) {

		SysLog sysLog = sysLogService.getById(id);
		if (sysLog == null) {

			return Result.fail("未找到对应实体");
		} else {

			boolean ok = sysLogService.removeById(id);
			if(ok) {
				return Result.success("删除成功!");
			}
		}

		return Result.success("删除失败!");
	}
	
	/**
	 * 批量，全部清空日志记录
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name="ids") String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别！");

		} else {
			if ("allclear".equals(ids)) {
				this.sysLogService.removeAll();
				return Result.fail("清除成功！");
			}

			this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
			return Result.fail("删除成功！");
		}
	}
}
