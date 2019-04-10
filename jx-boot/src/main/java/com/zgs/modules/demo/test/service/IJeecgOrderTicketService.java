package com.zgs.modules.demo.test.service;

import java.util.List;

import com.zgs.modules.demo.test.entity.JeecgOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单机票
 * @author： jx_boot
 * @date：   2019-02-15
 * @version： V1.0
 */
public interface IJeecgOrderTicketService extends IService<JeecgOrderTicket> {
	
	public List<JeecgOrderTicket> selectTicketsByMainId(String mainId);
}
