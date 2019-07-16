package com.zgs.modules.system.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.system.entity.SysDict;
import com.zgs.modules.system.model.SysDictTree;
import com.zgs.modules.system.service.ISysDictService;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 字典表 前端控制器
 *
 * @author zgs
 */
@RestController
@RequestMapping("/sys/dict")
@Slf4j
public class SysDictController {
	
	@Autowired
	private ISysDictService sysDictService;
	
	@GetMapping(value = "/list")
	public Result queryPageList(SysDict sysDict,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>(sysDict);
		Page<SysDict> page = new Page<SysDict>(pageNo,pageSize);

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

		IPage<SysDict> pageList = sysDictService.page(page, queryWrapper);
	    return Result.success(pageList);
	}
	
	/**
	 * 获取树形字典数据
	 * @param sysDict
	 * @return
	 */
	@GetMapping(value = "/treeList")
	public Result treeList(SysDict sysDict) {

		LambdaQueryWrapper<SysDict> query = new LambdaQueryWrapper<>();
		// 构造查询条件
		String dictName = sysDict.getDictName();
		if(oConvertUtils.isNotEmpty(dictName)) {
			query.like(true, SysDict::getDictName, dictName);
		}
		query.eq(true, SysDict::getIsDeleted, "1");
		query.orderByDesc(true, SysDict::getCreateTime);
		List<SysDict> list = sysDictService.list(query);
		List<SysDictTree> treeList = new ArrayList<>();
		for (SysDict node : list) {
			treeList.add(new SysDictTree(node));
		}

		return Result.success(treeList);
	}
	
	/**
	 * 获取字典数据
	 * @param dictCode
	 * @return
	 */
	@GetMapping(value = "/getDictItems/{dictCode}")
	public Result getDictItems(@PathVariable String dictCode) {

		log.info(" dictCode : "+ dictCode);
		List<Map<String,String>> ls = null;
		try {
			 ls = sysDictService.queryDictItemsByCode(dictCode);
			 return Result.success(ls);
		} catch (Exception e) {

			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	 * 获取字典数据
	 * @param dictCode
	 * @return
	 */
	@GetMapping(value = "/getDictText/{dictCode}/{key}")
	public Result getDictItems(@PathVariable("dictCode") String dictCode,
							   @PathVariable("key") String key) {

		log.info(" dictCode : "+ dictCode);
		String text = null;
		try {
			text = sysDictService.queryDictTextByKey(dictCode, key);
			return Result.success(text);

		} catch (Exception e) {

			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	 * 新增
	 * @param sysDict
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result add(@RequestBody SysDict sysDict) {

		try {

			sysDict.setCreateTime(new Date());
			sysDictService.save(sysDict);
			return Result.success("保存成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	 * 编辑
	 * @param sysDict
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result edit(@RequestBody SysDict sysDict) {

		SysDict sysdict = sysDictService.getById(sysDict.getId());
		if (sysdict == null) {
			return Result.fail("未找到对应实体");

		} else {

			sysDict.setUpdateTime(new Date());
			boolean ok = sysDictService.updateById(sysDict);
			if (ok) {
				return Result.success("编辑成功!");
			}
		}
		return Result.fail("编辑失败");
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result delete(@RequestParam(name="id",required=true) String id) {

		SysDict sysDict = sysDictService.getById(id);
		if (sysDict == null) {
			return Result.fail("未找到对应实体");

		} else {
			sysDict.setIsDeleted("1");
			boolean ok = sysDictService.updateById(sysDict);
			if (ok) {
				return Result.success("删除成功!");
			}
		}
		return Result.fail("删除失败");
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别！");

		} else {
			String[] id=ids.split(",");
			for (int i = 0; i < id.length; i++) {
				SysDict sysDict = sysDictService.getById(id[i]);
				sysDict.setIsDeleted("1");
				sysDictService.updateById(sysDict);
			}
			return Result.success("删除成功!");
		}
	}
}
