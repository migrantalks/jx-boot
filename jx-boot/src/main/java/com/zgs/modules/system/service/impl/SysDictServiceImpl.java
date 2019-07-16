package com.zgs.modules.system.service.impl;

import java.util.List;
import java.util.Map;

import com.zgs.modules.system.entity.SysDict;
import com.zgs.modules.system.mapper.SysDictMapper;
import com.zgs.modules.system.service.ISysDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 字典表 服务实现类
 * @author zgs
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

	@Autowired
	private SysDictMapper sysDictMapper;
	@Override
	public List<Map<String, String>> queryDictItemsByCode(String code) {
		return sysDictMapper.queryDictItemsByCode(code);
	}
	
	@Override
	public String queryDictTextByKey(String code, String key) {
		return sysDictMapper.queryDictTextByKey(code, key);
	}

}
