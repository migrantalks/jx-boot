package com.zgs.modules.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysUser;
import com.zgs.modules.system.entity.SysUserRole;
import com.zgs.modules.system.service.ISysUserRoleService;
import com.zgs.modules.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.PasswordUtil;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户 Controller
 * @author zgs
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
	
	@Autowired
	private ISysUserService sysUserService;
	
	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@GetMapping("/list")
	public Result queryPageList(SysUser user,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>(user);
		Page<SysUser> page = new Page<SysUser>(pageNo,pageSize);
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

		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
		return Result.success(pageList);
	}
	
	@PostMapping("/add")
	@RequiresRoles({"admin"})
	public Result add(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

		String selectedRoles = jsonObject.getString("selectedroles");
		try {

			String userId = JwtUtil.getUserToken(request, "userId");

			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//设置创建时间
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setIsDeleted("0");
			user.setCreateBy(userId);
			sysUserService.addUserWithRole(user, selectedRoles);
			return Result.success("添加成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	@PutMapping("/edit")
	@RequiresRoles({"admin"})
	public Result edit(@RequestBody JSONObject jsonObject) {

		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			if (sysUser == null) {
				return Result.fail("未找到对应实体");

			} else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
				sysUserService.editUserWithRole(user, roles);
				return Result.success("修改成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	@DeleteMapping(value = "/delete")
	@RequiresRoles({"admin"})
	public Result delete(@RequestParam(name="id",required=true) String id) {

		SysUser sysUser = sysUserService.getById(id);
		if (sysUser == null) {
			return Result.fail("未找到对应实体");

		} else {

			boolean ok = sysUserService.removeById(id);
			if (ok) {
				return Result.success("删除成功！");
			}
		}

		return Result.fail("删除失败！");
	}
	
	@DeleteMapping(value = "/deleteBatch")
	@RequiresRoles({"admin"})
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		if (ids == null || "".equals(ids.trim())) {
			return Result.fail("参数不识别!");

		} else {
			this.sysUserService.removeByIds(Arrays.asList(ids.split(",")));
			return Result.success("删除成功！");
		}
	}
	
	@RequestMapping(value = "/frozenBatch", method = {RequestMethod.POST,RequestMethod.GET})
	public Result frozenBatch(@RequestParam(name="ids",required=true) String ids,
									   @RequestParam(name="status",required=true) String status) {

		try {
			String[] arr = ids.split(",");
			for (String id : arr) {
				if (oConvertUtils.isNotEmpty(id)) {
					this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
							new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId,id));
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			return Result.fail(e.getMessage());
		}

		return Result.success("操作成功！");
	}
	
	@GetMapping(value = "/queryById")
	public Result queryById(@RequestParam(name="id",required=true) String id) {

		SysUser sysUser = sysUserService.getById(id);
		if (sysUser == null) {
			return Result.fail("未找到对应实体");

		} else {

			return Result.success(sysUser);
		}
	}
	
	@GetMapping(value = "/queryUserRole")
	public Result queryUserRole(@RequestParam(name="userid",required=true) String userid) {

		List<String> list = new ArrayList<String>();
		List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId,userid));
		if (userRole == null || userRole.size() <= 0) {
			return Result.fail("未找到用户相关角色信息");

		} else {
			for (SysUserRole sysUserRole : userRole) {
				list.add(sysUserRole.getRoleId());
			}
			return Result.success(list);
		}
	}
	
	
	/**
	  * 校验用户账号是否唯一
	  * 可以校验其他 需要检验什么就传什么。。。
	 * @param sysUser
	 * @return
	 */
	@GetMapping(value = "/checkOnlyUser")
	public Result checkUsername(SysUser sysUser) {

		String id = sysUser.getId();
		log.info("--验证用户信息是否唯一---id:"+id);
		try {
			SysUser oldUser = null;
			if (oConvertUtils.isNotEmpty(id)) {
				oldUser = sysUserService.getById(id);
			} else {
				sysUser.setId(null);
			}
			//通过传入信息查询新的用户信息
			SysUser newUser = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
			if (newUser != null) {
				//如果根据传入信息查询到用户了，那么就需要做校验了。
				if ( oldUser == null) {
					//oldUser为空=>新增模式=>只要用户信息存在则返回false
					return Result.fail("用户账号已存在");

				} else if(!id.equals(newUser.getId())) {
					//否则=>编辑模式=>判断两者ID是否一致-
					return Result.fail("用户账号已存在");
				}
			}
			
		} catch (Exception e) {

			return Result.fail(e.getMessage());
		}

		return Result.success("");
	}
	
	/**
	  * 修改密码
	 */
	@PutMapping(value = "/changPassword")
	@RequiresRoles({"admin"})
	public Result changPassword(@RequestBody SysUser sysUser) {

		String password = sysUser.getPassword();
		sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
		if (sysUser == null) {
			return Result.fail("未找到对应实体");

		} else {

			String salt = oConvertUtils.randomGen(8);
			sysUser.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(),password, salt);
			sysUser.setPassword(passwordEncode);
			this.sysUserService.updateById(sysUser);
			return Result.success(sysUser);
		}
	}
}
