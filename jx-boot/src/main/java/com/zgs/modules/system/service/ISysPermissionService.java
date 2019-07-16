package com.zgs.modules.system.service;

import java.util.List;

import com.zgs.modules.system.entity.SysPermission;
import com.zgs.modules.system.model.TreeModel;
import com.zgs.common.exception.ServiceException;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 菜单权限表 服务类
 *
 * @author zgs
 */
public interface ISysPermissionService extends IService<SysPermission> {
	
	List<TreeModel> queryListByParentId(String parentId);
	
	/**真实删除*/
	void deletePermission(String id) throws ServiceException;
	/**逻辑删除*/
	void deletePermissionLogical(String id) throws ServiceException;
	
	void addPermission(SysPermission sysPermission, String userId) throws ServiceException;
	
	void editPermission(SysPermission sysPermission, String userId) throws ServiceException;
	
	List<SysPermission> queryByUser(String username);
}
