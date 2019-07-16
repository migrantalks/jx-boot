package com.zgs.modules.system.controller;

import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysDept;
import com.zgs.modules.system.model.DepartIdModel;
import com.zgs.modules.system.model.SysDeptTreeModel;
import com.zgs.modules.system.service.ISysDeptService;
import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.authz.annotation.RequiresRoles;
import com.zgs.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 部门 Controller
 * @author zgs
 */
@RestController
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

	@Autowired
	private ISysDeptService sysDepartService;

	/**
	 * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	 * 
	 * @return
	 */
	@GetMapping("/queryTreeList")
	public Result queryTreeList() {

		try {

			List<SysDeptTreeModel> list = sysDepartService.queryTreeList();
			return Result.success(list);

		} catch (Exception e) {

			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}

	/**
	 * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
	 * 
	 * @param sysDept
	 * @return
	 */
	@PostMapping("/add")
	@RequiresRoles({"admin"})
	public Result add(@RequestBody SysDept sysDept, HttpServletRequest request) {

		String userId = JwtUtil.getUserToken(request, "userId");
		try {

			sysDept.setCreateBy(userId);
			sysDepartService.saveDeptData(sysDept, userId);
			return Result.success("添加成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());

			return Result.fail("操作失败");
		}
	}

	/**
	 * 编辑数据 编辑部门的部分数据,并保存到数据库
	 * 
	 * @param sysDepart
	 * @return
	 */
	@PutMapping("/edit")
	@RequiresRoles({"admin"})
	public Result edit(@RequestBody SysDept sysDepart, HttpServletRequest request) {

		String userId = JwtUtil.getUserToken(request, "userId");
		sysDepart.setUpdateBy(userId);

		SysDept sysDepartEntity = sysDepartService.getById(sysDepart.getId());
		if (sysDepartEntity == null) {
			return Result.success("未找到对应实体");
		} else {

			sysDepartService.updateDeptDataById(sysDepart, userId);
			return Result.fail("修改成功！");
		}
	}
	
	 /**
     *   通过id删除
    * @param id
    * @return
    */
   @DeleteMapping("/delete")
   @RequiresRoles({"admin"})
   public Result delete(@RequestParam(name="id", required=true) String id) {

       SysDept sysDepart = sysDepartService.getById(id);
       if (sysDepart == null) {
		   return Result.success("未找到对应实体");
       } else {
           boolean ok = sysDepartService.removeById(id);
           if(ok) {
			   return Result.fail("删除成功！");
           }
       }

	   return Result.fail("删除失败！");
   }

	/**
	 * 批量删除 根据前端请求的多个ID,对数据库执行删除相关部门数据的操作
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/deleteBatch")
	@RequiresRoles({"admin"})
	public Result deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别");

		} else {

			this.sysDepartService.removeByIds(Arrays.asList(ids.split(",")));
			return Result.success("删除成功！");
		}
	}

	/**
	 * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
	 * 
	 * @return
	 */
	@GetMapping("/queryIdTree")
	public Result queryIdTree() {

		try {

			List<DepartIdModel> idList = FindDeptChildrenUtil.wrapDepartIdModel();
			return Result.success(idList);

		} catch(Exception e) {

			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	 
	/**
	 * 部门搜索功能方法,根据关键字模糊搜索相关部门
	 * @param keyWord
	 * @return
	 */
	@GetMapping("/searchBy")
	public Result searchBy(@RequestParam(name = "keyWord", required = true) String keyWord) {

		try {

			List<SysDeptTreeModel> treeList = this.sysDepartService.searchBy(keyWord);
			if (treeList.size() == 0 || treeList == null) {
				throw new Exception();
			}
			return Result.success(treeList);

		} catch (Exception e) {

			e.fillInStackTrace();
			return Result.fail("查询失败或没有您想要的任何数据!");
		}
	}
}
