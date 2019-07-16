package com.zgs.modules.system.service.impl;

import javax.annotation.Resource;

import com.zgs.modules.system.entity.SysLog;
import com.zgs.modules.system.mapper.SysLogMapper;
import com.zgs.modules.system.service.ISysLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 系统日志表 服务实现类
 *
 * @author zgs
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

	@Resource
	private SysLogMapper sysLogMapper;
	
	/**
	 * @功能：清空所有日志记录
	 */
	@Override
	public void removeAll() {
		sysLogMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return sysLogMapper.findTotalVisitCount();
	}

	@Override
	public Long findTodayVisitCount() {
		return sysLogMapper.findTodayVisitCount();
	}

	@Override
	public Long findTodayIp() {
		return sysLogMapper.findTodayIp();
	}

}
