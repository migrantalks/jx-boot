package com.zgs.modules.quartz.service.impl;

import java.util.List;

import com.zgs.modules.quartz.entity.QuartzJob;
import com.zgs.modules.quartz.mapper.QuartzJobMapper;
import com.zgs.modules.quartz.entity.QuartzJob;
import com.zgs.modules.quartz.mapper.QuartzJobMapper;
import com.zgs.modules.quartz.service.IQuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 定时任务在线管理
 * @author： jx_boot
 * @date：   2019-01-02
 * @version： V1.0
 */
@Service
public class QuartzJobServiceImpl extends ServiceImpl<QuartzJobMapper, QuartzJob> implements IQuartzJobService {
	@Autowired
	private QuartzJobMapper quartzJobMapper;

	@Override
	public List<QuartzJob> findByJobClassName(String jobClassName) {
		return quartzJobMapper.findByJobClassName(jobClassName);
	}

}
