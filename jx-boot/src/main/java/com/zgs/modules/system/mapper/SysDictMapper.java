package com.zgs.modules.system.mapper;

import java.util.List;
import java.util.Map;

import com.zgs.modules.system.entity.SysDict;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 字典表 Mapper 接口
 *
 * @author zgs
 */
public interface SysDictMapper extends BaseMapper<SysDict> {
	
	List<Map<String,String>> queryDictItemsByCode(@Param("code") String code);
	
	String queryDictTextByKey(@Param("code") String code,@Param("key") String key);

}
