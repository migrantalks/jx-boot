package com.zgs.modules.system.controller;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysAnnouncement;
import com.zgs.modules.system.service.ISysAnnouncementService;
import com.zgs.common.api.vo.Result;
import com.zgs.common.constant.CommonConstant;
import com.zgs.common.constant.CommonSendStatus;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

 /**
 * 系统通告
 * @author： zgs
 */
@RestController
@RequestMapping("/sys/annountCement")
@Slf4j
public class SysAnnouncementController {
	@Autowired
	private ISysAnnouncementService sysAnnouncementService;
	
	/**
	  * 分页列表查询
	 * @param sysAnnouncement
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result queryPageList(SysAnnouncement sysAnnouncement,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {

		sysAnnouncement.setIsDeleted("0");
		QueryWrapper<SysAnnouncement> queryWrapper = new QueryWrapper<SysAnnouncement>(sysAnnouncement);
		Page<SysAnnouncement> page = new Page<SysAnnouncement>(pageNo, pageSize);
		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			}else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}
		IPage<SysAnnouncement> pageList = sysAnnouncementService.page(page, queryWrapper);
		return Result.success(pageList);
	}
	
	/**
	 * 添加
	 * @param sysAnnouncement
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result add(@RequestBody SysAnnouncement sysAnnouncement) {

		try {
			sysAnnouncement.setIsDeleted(CommonConstant.DEL_FLAG_0.toString());
			sysAnnouncement.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);//未发布
			sysAnnouncementService.save(sysAnnouncement);
			return Result.success("添加成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	  *  编辑
	 * @param sysAnnouncement
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result edit(@RequestBody SysAnnouncement sysAnnouncement) {

		SysAnnouncement sysAnnouncementEntity = sysAnnouncementService.getById(sysAnnouncement.getId());
		if (sysAnnouncementEntity == null) {
			return Result.fail("未找到对应实体");
		} else {
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				return Result.success("修改成功!");
			}
		}

		return Result.fail("修改失败");
	}
	
	/**
	  *  通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result delete(@RequestParam(name="id",required=true) String id) {

		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if (sysAnnouncement == null) {
			return Result.fail("未找到对应实体");
		} else {

			sysAnnouncement.setIsDeleted(CommonConstant.DEL_FLAG_1.toString());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				return Result.success("删除成功!");
			}
		}

		return Result.fail("删除失败!");
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {

		if (ids == null || "".equals(ids.trim())) {

			return Result.fail("参数不识别！");

		} else {

			String[] id = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				SysAnnouncement announcement = sysAnnouncementService.getById(id[i]);
				announcement.setIsDeleted(CommonConstant.DEL_FLAG_1.toString());
				sysAnnouncementService.updateById(announcement);
			}
			return Result.success("删除成功！");
		}
	}


	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result queryById(@RequestParam(name="id",required=true) String id) {

		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if (sysAnnouncement == null) {
			return Result.fail("未找到对应实体！");

		} else {

			return Result.success(sysAnnouncement);
		}
	}
	
	/**
	 *	 更新发布操作
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/doReleaseData")
	public Result doReleaseData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {

		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if (sysAnnouncement == null) {
			return Result.fail("未找到对应实体！");

		} else {
			sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);//发布中
			sysAnnouncement.setSendTime(new Date());
			String userId = JwtUtil.getUserToken(request, "userId");
			sysAnnouncement.setSender(userId);
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				return Result.success("该系统通知发布成功");
			}
		}

		return Result.fail("该系统通知发布失败");
	}
	
	/**
	 *	 更新撤销操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
	public Result doReovkeData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {

		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if (sysAnnouncement == null) {
			return Result.fail("未找到对应实体！");

		} else {
			sysAnnouncement.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);//撤销发布
			sysAnnouncement.setCancelTime(new Date());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				return Result.success("该系统通知撤销成功");
			}
		}

		return Result.fail("该系统通知撤销失败");
	}
}
