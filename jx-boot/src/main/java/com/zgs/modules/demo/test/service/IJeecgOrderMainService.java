package com.zgs.modules.demo.test.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.zgs.modules.demo.test.entity.JeecgOrderCustomer;
import com.zgs.modules.demo.test.entity.JeecgOrderMain;
import com.zgs.modules.demo.test.entity.JeecgOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单
 * @author： jx_boot
 * @date：   2019-02-15
 * @version： V1.0
 */
public interface IJeecgOrderMainService extends IService<JeecgOrderMain> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(JeecgOrderMain jeecgOrderMain,List<JeecgOrderCustomer> jeecgOrderCustomerList,List<JeecgOrderTicket> jeecgOrderTicketList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(JeecgOrderMain jeecgOrderMain,List<JeecgOrderCustomer> jeecgOrderCustomerList,List<JeecgOrderTicket> jeecgOrderTicketList);
	
	/**
	 * 删除一对多
	 * @param jformOrderMain
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 * @param jformOrderMain
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
}
