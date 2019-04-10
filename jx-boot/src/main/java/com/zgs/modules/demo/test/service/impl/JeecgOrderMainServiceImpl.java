package com.zgs.modules.demo.test.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.zgs.modules.demo.test.mapper.JeecgOrderCustomerMapper;
import com.zgs.modules.demo.test.mapper.JeecgOrderMainMapper;
import com.zgs.modules.demo.test.mapper.JeecgOrderTicketMapper;
import com.zgs.modules.demo.test.service.IJeecgOrderMainService;
import com.zgs.modules.demo.test.entity.JeecgOrderCustomer;
import com.zgs.modules.demo.test.entity.JeecgOrderMain;
import com.zgs.modules.demo.test.entity.JeecgOrderTicket;
import com.zgs.modules.demo.test.mapper.JeecgOrderCustomerMapper;
import com.zgs.modules.demo.test.mapper.JeecgOrderMainMapper;
import com.zgs.modules.demo.test.mapper.JeecgOrderTicketMapper;
import com.zgs.modules.demo.test.service.IJeecgOrderMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单
 * @author： jx_boot
 * @date：   2019-02-15
 * @version： V1.0
 */
@Service
public class JeecgOrderMainServiceImpl extends ServiceImpl<JeecgOrderMainMapper, JeecgOrderMain> implements IJeecgOrderMainService {

	@Autowired
	private JeecgOrderMainMapper jeecgOrderMainMapper;
	@Autowired
	private JeecgOrderCustomerMapper jeecgOrderCustomerMapper;
	@Autowired
	private JeecgOrderTicketMapper jeecgOrderTicketMapper;
	
	@Override
	@Transactional
	public void saveMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) {
		jeecgOrderMainMapper.insert(jeecgOrderMain);
		for(JeecgOrderCustomer entity:jeecgOrderCustomerList) {
			entity.setOrderId(jeecgOrderMain.getId());
			jeecgOrderCustomerMapper.insert(entity);
		}
		for(JeecgOrderTicket entity:jeecgOrderTicketList) {
			entity.setOrderId(jeecgOrderMain.getId());
			jeecgOrderTicketMapper.insert(entity);
		}
	}

	@Override
	@Transactional
	public void updateMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) {
		jeecgOrderMainMapper.updateById(jeecgOrderMain);
		
		//1.先删除子表数据
		jeecgOrderTicketMapper.deleteTicketsByMainId(jeecgOrderMain.getId());
		jeecgOrderCustomerMapper.deleteCustomersByMainId(jeecgOrderMain.getId());
		
		//2.子表数据重新插入
		for(JeecgOrderCustomer entity:jeecgOrderCustomerList) {
			entity.setOrderId(jeecgOrderMain.getId());
			jeecgOrderCustomerMapper.insert(entity);
		}
		for(JeecgOrderTicket entity:jeecgOrderTicketList) {
			entity.setOrderId(jeecgOrderMain.getId());
			jeecgOrderTicketMapper.insert(entity);
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		jeecgOrderMainMapper.deleteById(id);
		jeecgOrderTicketMapper.deleteTicketsByMainId(id);
		jeecgOrderCustomerMapper.deleteCustomersByMainId(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			jeecgOrderMainMapper.deleteById(id);
			jeecgOrderTicketMapper.deleteTicketsByMainId(id.toString());
			jeecgOrderCustomerMapper.deleteCustomersByMainId(id.toString());
		}
	}

}
