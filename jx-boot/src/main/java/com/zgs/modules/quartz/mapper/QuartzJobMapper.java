package com.zgs.modules.quartz.mapper;

import java.util.List;

import com.zgs.modules.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;
import com.zgs.modules.quartz.entity.QuartzJob;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 定时任务在线管理
 * @author： jx_boot
 * @date：   2019-01-02
 * @version： V1.0
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

	public List<QuartzJob> findByJobClassName(@Param("jobClassName") String jobClassName);

}
