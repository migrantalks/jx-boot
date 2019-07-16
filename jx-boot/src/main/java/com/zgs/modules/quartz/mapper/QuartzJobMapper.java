package com.zgs.modules.quartz.mapper;

import java.util.List;

import com.zgs.modules.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 定时任务在线管理
 * @author： zgs
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

	List<QuartzJob> findByJobClassName(@Param("jobClassName") String jobClassName);

}
