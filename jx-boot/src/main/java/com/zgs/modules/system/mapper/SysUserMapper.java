package com.zgs.modules.system.mapper;

import com.zgs.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import com.zgs.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	 * @param username
	 * @return
	 */
	public SysUser getUserByName(@Param("username") String username);
}
