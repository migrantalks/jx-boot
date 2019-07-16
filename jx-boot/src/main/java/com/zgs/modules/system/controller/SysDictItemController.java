package com.zgs.modules.system.controller;


import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.system.entity.SysDictItem;
import com.zgs.modules.system.service.ISysDictItemService;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 前端控制器
 * @author zgs
 */
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
public class SysDictItemController {

	@Autowired
	private ISysDictItemService sysDictItemService;
	
	/**
	 * @功能：查询字典数据
	 * @param sysDictItem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result queryPageList(SysDictItem sysDictItem,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<SysDictItem> queryWrapper = new QueryWrapper<SysDictItem>(sysDictItem);
		Page<SysDictItem> page = new Page<SysDictItem>(pageNo,pageSize);

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

		IPage<SysDictItem> pageList = sysDictItemService.page(page, queryWrapper);
		return Result.success(pageList);
	}
	
	/**
	 * 新增
	 * @param sysDictItem
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result add(@RequestBody SysDictItem sysDictItem) {

		try {
			sysDictItem.setCreateTime(new Date());
			sysDictItemService.save(sysDictItem);
			return Result.success("保存成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	 * 编辑
	 * @param sysDictItem
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result edit(@RequestBody SysDictItem sysDictItem) {

		SysDictItem sysdict = sysDictItemService.getById(sysDictItem.getId());
		if (sysdict == null) {
			return Result.fail("未找到对应实体");

		} else {
			sysDictItem.setUpdateTime(new Date());
			boolean ok = sysDictItemService.updateById(sysDictItem);
			if (ok) {
				return Result.success("编辑成功!");
			}
		}
		return Result.fail("编辑失败");
	}
	
	/**
	 * 删除字典数据
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result delete(@RequestParam(name="id",required=true) String id) {

		SysDictItem joinSystem = sysDictItemService.getById(id);
		if (joinSystem == null) {
			return Result.fail("未找到对应实体");

		} else {
			boolean ok = sysDictItemService.removeById(id);
			if (ok) {
				return Result.success("删除成功!");
			}
		}
		return Result.fail("删除失败");
	}
	
	/**
	 * 批量删除字典数据
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		if (ids== null || "".equals(ids.trim())) {
			return Result.fail("参数不识别");

		} else {
			this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
			return Result.success("删除成功!");
		}
	}
}
