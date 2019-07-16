package com.zgs.modules.system.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.zgs.modules.shiro.authc.util.JwtUtil;
import com.zgs.modules.system.entity.SysPermission;
import com.zgs.modules.system.entity.SysRolePermission;
import com.zgs.modules.system.model.SysPermissionTree;
import com.zgs.modules.system.model.TreeModel;
import com.zgs.modules.system.service.ISysPermissionService;
import com.zgs.modules.system.service.ISysRolePermissionService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import com.zgs.common.api.vo.Result;
import com.zgs.common.util.MD5Util;
import com.zgs.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 菜单权限表 前端控制器
 * @author zgs
 */
@Slf4j
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController {
	
	@Autowired
	private ISysPermissionService sysPermissionService;
	
	@Autowired
	private ISysRolePermissionService sysRolePermissionService;
	
	/**
	 * 加载数据节点
	 * @return
	 */
	@GetMapping("/list")
	public Result list() {

		try {

			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getIsDeleted, 0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			List<SysPermissionTree> treeList = new ArrayList<>();
			getTreeList(treeList, list, null);

			return Result.success(treeList);

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	
	/**
	 *  查询用户的权限
	 * @return
	 */
	@GetMapping("/queryByUser")
	public Result queryByUser(HttpServletRequest req) {

		try {
			String username = req.getParameter("username");
			List<SysPermission> metaList = sysPermissionService.queryByUser(username);
			JSONArray jsonArray = new JSONArray();
			this.getPermissionJsonArray(jsonArray, metaList, null);
			return Result.success(jsonArray);

		} catch (Exception e) {

			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	

	@PostMapping("/add")
	@RequiresRoles({"admin"})
	public Result add(@RequestBody SysPermission permission, HttpServletRequest request) {

		try {

			String userId = JwtUtil.getUserToken(request, "userId");
			sysPermissionService.addPermission(permission, userId);
			return Result.success("添加成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	@PutMapping("/edit")
	@RequiresRoles({"admin"})
	public Result edit(@RequestBody SysPermission permission, HttpServletRequest request) {

		try {

			String userId = JwtUtil.getUserToken(request, "userId");
			sysPermissionService.editPermission(permission, userId);
			return Result.success("修改成功！");

		} catch (Exception e) {

			e.printStackTrace();
			log.info(e.getMessage());
			return Result.fail(e.getMessage());
		}
	}
	
	@DeleteMapping("/delete")
	@RequiresRoles({"admin"})
	public Result delete(@RequestParam(name="id") String id) {

		try {
			sysPermissionService.deletePermission(id);
			return Result.success("删除成功！");

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	
	@DeleteMapping("/deleteBatch")
	@RequiresRoles({"admin"})
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			String arr[] = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					sysPermissionService.deletePermission(id);
				}
			}
			return Result.success("删除成功！");

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	  *  获取全部的权限树
	 * @return
	 */
	@GetMapping(value = "/queryTreeList")
	public Result queryTreeList() {

		//全部权限ids
		List<String> ids = new ArrayList<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getIsDeleted, 0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			for(SysPermission sysPer : list) {
				ids.add(sysPer.getId());
			}
			
			List<TreeModel> treeList = new ArrayList<>();
			getTreeModelList(treeList, list, null);
			
			Map<String,Object> resMap = new HashMap<String,Object>();
			resMap.put("treeList", treeList); //全部树节点数据
			resMap.put("ids", ids);//全部树ids
			return Result.success(resMap);

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}

	/**
	 * 异步加载数据节点
	 * @return
	 */
	@GetMapping("/queryListAsync")
	public Result queryAsync(@RequestParam(name="pid",required=false) String parentId) {

		try {
			List<TreeModel> list = sysPermissionService.queryListByParentId(parentId);
			if (list == null || list.size() <= 0) {
				return Result.fail("未找到角色信息");

			} else {

				return Result.success(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	  *  查询角色授权
	 * @return
	 */
	@GetMapping("/queryRolePermission")
	public Result queryRolePermission(@RequestParam(name="roleId",required=true) String roleId) {

		try {
			List<SysRolePermission> list = sysRolePermissionService.list(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId));
			return Result.success(list.stream().map(SysRolePermission -> String.valueOf(SysRolePermission.getPermissionId())).collect(Collectors.toList()));

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	/**
	  *  保存角色授权
	 * @return
	 */
	@PostMapping("/saveRolePermission")
	public Result saveRolePermission(@RequestBody JSONObject json) {

		try {
			String roleId = json.getString("roleId");
			String permissionIds = json.getString("permissionIds");
			this.sysRolePermissionService.saveRolePermission(roleId, permissionIds);
			return Result.success("保存成功！");

		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail(e.getMessage());
		}
	}
	
	
	private void getTreeList(List<SysPermissionTree> treeList,List<SysPermission> metaList,SysPermissionTree temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			SysPermissionTree tree = new SysPermissionTree(permission);
			if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if(tree.getIsLeaf()==0) {
					getTreeList(treeList, metaList, tree);
				}
			}else if(temp!=null && tempPid!=null && tempPid.equals(temp.getId())){
				temp.getChildren().add(tree);
				if(tree.getIsLeaf()==0) {
					getTreeList(treeList, metaList, tree);
				}
			}
			
		}
	}
	
	private void getTreeModelList(List<TreeModel> treeList,List<SysPermission> metaList,TreeModel temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission);
			if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if(permission.getIsLeaf()==0) {
					getTreeModelList(treeList, metaList, tree);
				}
			}else if(temp!=null && tempPid!=null && tempPid.equals(temp.getKey())){
				temp.getChildren().add(tree);
				if(permission.getIsLeaf()==0) {
					getTreeModelList(treeList, metaList, tree);
				}
			}
			
		}
	}
	/**
	  *  获取菜单JSON数组
	 * @param jsonArray
	 * @param metaList
	 * @param parentJson
	 */
	private void getPermissionJsonArray(JSONArray jsonArray,List<SysPermission> metaList,JSONObject parentJson) {
		for (SysPermission permission : metaList) {
			if(permission.getMenuType()==null) {
				continue;
			}
			String tempPid = permission.getParentId();
			JSONObject json = getPermissionJsonObject(permission);
			if(parentJson==null && oConvertUtils.isEmpty(tempPid)) {
				jsonArray.add(json);
				if(permission.getIsLeaf()==0) {
					getPermissionJsonArray(jsonArray, metaList, json);
				}
			}else if(parentJson!=null && oConvertUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))){
				if(permission.getMenuType()==0) {
					JSONObject metaJson = parentJson.getJSONObject("meta");
					if(metaJson.containsKey("permissionList")) {
						metaJson.getJSONArray("permissionList").add(json);
					}else {
						JSONArray permissionList = new JSONArray();
						permissionList.add(json);
						metaJson.put("permissionList", permissionList);
					}
					
				}else if(permission.getMenuType()==1) {
					if(parentJson.containsKey("children")) {
						parentJson.getJSONArray("children").add(json);
					}else {
						JSONArray children = new JSONArray();
						children.add(json);
						parentJson.put("children", children);
					}
					
					if(permission.getIsLeaf()==0) {
						getPermissionJsonArray(jsonArray, metaList, json);
					}
				}
			}
			
		
		}
	}
	
	private JSONObject getPermissionJsonObject(SysPermission permission) {
		JSONObject json = new JSONObject();
		//类型(0：一级菜单 1：子菜单  2：按钮)
		if(permission.getMenuType()==2) {
			json.put("action", permission.getPerms());
			json.put("describe", permission.getName());
		}else if(permission.getMenuType()==0||permission.getMenuType()==1) {
			json.put("id", permission.getId());
			if(permission.getUrl()!=null&&(permission.getUrl().startsWith("http://")||permission.getUrl().startsWith("https://"))) {
				json.put("path", MD5Util.MD5Encode(permission.getUrl(), "utf-8"));
			}else {
				json.put("path", permission.getUrl());
			}
			
			//重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
			json.put("name", urlToRouteName(permission.getUrl()));
			
			//是否隐藏路由，默认都是显示的
			if(permission.isHidden()) {
				json.put("hidden",true);
			}
			//聚合路由
			if(permission.isAlwaysShow()) {
				json.put("alwaysShow",true);
			}
			json.put("component", permission.getComponent());
			JSONObject meta = new JSONObject();
			meta.put("title", permission.getName());
			if(oConvertUtils.isEmpty(permission.getParentId())) {
				//一级菜单跳转地址
				json.put("redirect",permission.getRedirect());
				meta.put("icon", oConvertUtils.getString(permission.getIcon(), ""));
			}else {
				meta.put("icon", oConvertUtils.getString(permission.getIcon(), ""));
			}
			if(permission.getUrl()!=null&&(permission.getUrl().startsWith("http://")||permission.getUrl().startsWith("https://"))) {
				meta.put("url", permission.getUrl());
			}
			json.put("meta", meta);
		}
		
		return json;
	}
	
	/**
	  * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-）
	  * 举例： URL = /isystem/role
	  *     RouteName = isystem-role
	 * @return
	 */
	private String urlToRouteName(String url) {
		if(oConvertUtils.isNotEmpty(url)) {
			if(url.startsWith("/")) {
				url = url.substring(1);
			}
			url = url.replace("/", "-");
			return url;
		}else {
			return null;
		}
	}
	
}