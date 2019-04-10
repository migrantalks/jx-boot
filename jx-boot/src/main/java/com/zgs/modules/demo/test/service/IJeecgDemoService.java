package com.zgs.modules.demo.test.service;

import com.zgs.modules.demo.test.entity.JeecgDemo;
import com.zgs.modules.demo.test.entity.JeecgDemo;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: jeecg 测试demo
 * @author： jx_boot
 * @date：   2018-12-29
 * @version： V1.0
 */
public interface IJeecgDemoService extends IService<JeecgDemo> {
	public void testTran();
	
	public JeecgDemo getByIdCacheable(String id);
}
