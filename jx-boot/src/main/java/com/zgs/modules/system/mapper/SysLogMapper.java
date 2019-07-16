package com.zgs.modules.system.mapper;

import com.zgs.modules.system.entity.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 系统日志表 Mapper 接口
 *
 * @author zgs
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

	/**
	 * 清空所有日志记录
	 */
	void removeAll();

	/**
	 * 获取系统总访问次数
	 *
	 * @return Long
	 */
	Long findTotalVisitCount();

	/**
	 * 获取系统今日访问次数
	 *
	 * @return Long
	 */
	Long findTodayVisitCount();

	/**
	 * 获取系统今日访问 IP数
	 *
	 * @return Long
	 */
	Long findTodayIp();

}
