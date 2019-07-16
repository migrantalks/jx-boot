package com.zgs.modules.system.service;

import java.util.List;

import com.zgs.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表 服务类
 *
 * @author zgs
 */
public interface ISysUserService extends IService<SysUser> {
	
	SysUser getUserByName(String username);
	
	/**
	 * 添加用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	void addUserWithRole(SysUser user,String roles);
	
	
	/**
	 * 修改用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	void editUserWithRole(SysUser user,String roles);

	/**
	 * 获取用户的授权角色
	 * @param username
	 * @return
	 */
	List<String> getRole(String username);
}
