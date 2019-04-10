package com.zgs.modules.system.service.impl;

import com.zgs.modules.system.entity.SysRole;
import com.zgs.modules.system.mapper.SysRoleMapper;
import com.zgs.modules.system.service.ISysRoleService;
import com.zgs.modules.system.entity.SysRole;
import com.zgs.modules.system.mapper.SysRoleMapper;
import com.zgs.modules.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author scott
 * @since 2018-12-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {


}
