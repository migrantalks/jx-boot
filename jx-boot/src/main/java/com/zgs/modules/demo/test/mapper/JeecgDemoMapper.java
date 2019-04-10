package com.zgs.modules.demo.test.mapper;

import java.util.List;
import com.zgs.modules.demo.test.entity.JeecgDemo;
import org.springframework.data.repository.query.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: jeecg 测试demo
 * @author： jx_boot
 * @date：   2018-12-29
 * @version： V1.0
 */
public interface JeecgDemoMapper extends BaseMapper<JeecgDemo> {

	public List<JeecgDemo> getDemoByName(@Param("name") String name);

}
