package com.zgs.modules.system.service;

import java.util.List;
import java.util.Map;

import com.zgs.modules.system.entity.SysDict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 字典表 服务类
 * @author zgs
 */
public interface ISysDictService extends IService<SysDict> {

	List<Map<String,String>> queryDictItemsByCode(String code);
	
	String queryDictTextByKey(String code,String key);
}
