package com.zgs.modules.system.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysRole;
import com.zgs.modules.system.service.ISysRoleService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * 角色 前端控制器
 * @author zgs
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {

	@Autowired
	private ISysRoleService sysRoleService;

	/**
	  * 分页列表查询
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping("/list")
	public Result queryPageList(SysRole role,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>(role);
		Page<SysRole> page = new Page<SysRole>(pageNo,pageSize);

		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if ("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			} else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}

		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		return Result.success(pageList);
	}
	
	/**
	  *   添加
	 * @param role
	 * @return
	 */
	@PostMapping("/add")
	public Result add(@RequestBody SysRole role, HttpServletRequest request) {

		try {

			String userId = JwtUtil.getUserToken(request, "userId");

			role.setCreateBy(userId);
			role.setCreateTime(new Date());
			sysRoleService.save(role);
			return Result.success("添加成功！");

		} catch (Exception e) {

			e.printStackTrace();
			return Result.fail("操作失败");
		}
	}
	
	/**
	 *  编辑
	 * @param role
	 * @return
	 */
	@PutMapping(value = "/edit")
	@RequiresRoles({"admin"})
	public Result edit(@RequestBody SysRole role, HttpServletRequest request) {

		SysRole sysrole = sysRoleService.getById(role.getId());
		if (sysrole == null) {
			return Result.fail("未找到对应实体");
		} else {

			String userId = JwtUtil.getUserToken(request, "userId");
			role.setUpdateBy(userId);
			role.setUpdateTime(new Date());

			boolean ok = sysRoleService.updateById(role);
			if (ok) {
				return Result.success("修改成功！");
			}
		}

		return Result.fail("修改失败");
	}
	
	/**
	 *  通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete")
	@RequiresRoles({"admin"})
	public Result delete(@RequestParam(name="id",required=true) String id) {

		SysRole sysrole = sysRoleService.getById(id);
		if (sysrole == null) {
			return Result.fail("未找到对应实体");
		} else {
			boolean ok = sysRoleService.removeById(id);
			if(ok) {
				return Result.success("删除成功！");
			}
		}

		return Result.fail("删除失败！");
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/deleteBatch")
	@RequiresRoles({"admin"})
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别！");
		} else {
			this.sysRoleService.removeByIds(Arrays.asList(ids.split(",")));
			return Result.success("删除成功！");
		}
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result queryById(@RequestParam(name="id",required=true) String id) {

		SysRole sysrole = sysRoleService.getById(id);
		if (sysrole == null) {
			return Result.fail("未找到对应实体");
		} else {
			return Result.success(sysrole);
		}
	}
	
	@GetMapping(value = "/queryAll")
	public Result queryAll() {

		List<SysRole> list = sysRoleService.list();
		if (list == null || list.size() <= 0) {
			return Result.fail("未找到角色信息");
		} else {

			return Result.success(list);
		}
	}
	
	/**
	  * 校验角色编码唯一
	 */
	@RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
	public Result checkRoleCode(String id,String roleCode) {

		try {
			SysRole role = null;
			if (oConvertUtils.isNotEmpty(id)) {
				role = sysRoleService.getById(id);
			}
			SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
			if (newRole != null) {
				//如果根据传入的roleCode查询到信息了，那么就需要做校验了。
				if (role == null) {
					//role为空=>新增模式=>只要roleCode存在则返回false
					return Result.fail("角色编码已存在");

				} else if(!id.equals(newRole.getId())) {
					//否则=>编辑模式=>判断两者ID是否一致-
					return Result.fail("角色编码已存在");
				}
			}
		} catch (Exception e) {
			return Result.fail(e.getMessage());
		}

		return Result.success("");
	}
}
