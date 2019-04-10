package com.zgs.modules.demo.test.vo;

import java.util.List;

import com.zgs.modules.demo.test.entity.JeecgOrderTicket;
import com.zgs.modules.demo.test.entity.JeecgOrderCustomer;
import com.zgs.modules.demo.test.entity.JeecgOrderMain;
import com.zgs.modules.demo.test.entity.JeecgOrderTicket;
import lombok.Data;

@Data
public class JeecgOrderMainPage {
	
	private JeecgOrderMain jeecgOrderMain;
	private List<JeecgOrderCustomer> jeecgOrderCustomerList;
	private List<JeecgOrderTicket> jeecgOrderTicketList;
	
}
