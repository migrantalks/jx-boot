package com.zgs.modules.system.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysUser;
import com.zgs.modules.system.model.SysLoginModel;
import com.zgs.modules.system.service.ISysLogService;
import com.zgs.modules.system.service.ISysUserService;
import com.zgs.common.api.vo.Result;
import com.zgs.common.constant.CommonConstant;
import com.zgs.common.system.api.ISysBaseAPI;
import com.zgs.common.util.PasswordUtil;
import com.zgs.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 登录
 * @author zgs
 */
@RestController
@RequestMapping("/sys")
@Api("用户登录")
public class LoginController {

	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ISysLogService logService;
	@Autowired
    private RedisUtil redisUtil;
	

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation("登录接口")
	public Result login(@RequestBody SysLoginModel sysLoginModel) {

		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		SysUser sysUser = sysUserService.getUserByName(username);
		if (sysUser == null) {

			sysBaseAPI.addLog("登录失败，用户名:"+username+"不存在！", CommonConstant.LOG_TYPE_1, null);
			return Result.fail("该用户不存在");

		} else {
			//密码验证
			String userPassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
			String sysPassword = sysUser.getPassword();
			if (!sysPassword.equals(userPassword)) {
				return Result.fail("用户名或密码不正确");
			}

			String userId = sysUser.getId();

			//生成token
			String token = JwtUtil.sign(userId, username, sysPassword);
			redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
			 //设置超时时间
			redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME/1000);
			
			JSONObject obj = new JSONObject();
			obj.put("token", token);
			obj.put("userInfo", sysUser);
			sysBaseAPI.addLog("用户名: "+username+",登录成功！", CommonConstant.LOG_TYPE_1, null);

			return Result.success(obj);
		}
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	public Result loginfo() {

		JSONObject obj = new JSONObject();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount();
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp();
		obj.put("todayIp", todayIp);

		return Result.success(obj);
	}

}
