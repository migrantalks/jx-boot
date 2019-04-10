package com.zgs.modules.system.service;

import com.zgs.modules.system.entity.SysRolePermission;
import com.zgs.modules.system.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author scott
 * @since 2018-12-21
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {
	
	/**
	 * 保存授权/先删后增
	 * @param roleId
	 * @param permissionIds
	 */
	public void saveRolePermission(String roleId,String permissionIds);

}
