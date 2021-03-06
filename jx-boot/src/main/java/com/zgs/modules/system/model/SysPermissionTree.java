package com.zgs.modules.system.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.zgs.modules.system.entity.SysPermission;

public class SysPermissionTree implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	private String key;

	/**
	 * 父id
	 */
	private String parentId;

	/**
	 * 菜单名称
	 */
	private String name;

	/**
	 * 菜单权限编码
	 */
	private String perms;

	/**
	 * 菜单图标
	 */
	private String icon;

	/**
	 * 组件
	 */
	private String component;

	/**
	 * 跳转网页链接
	 */
	private String url;
	
	/**
	 * 一级菜单跳转地址
	 */
	private String redirect;

	/**
	 * 菜单排序
	 */
	private Integer sortNo;

	/**
	 * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
	 */
	private Integer menuType;

	/**
	 * 是否叶子节点: 1:是 0:不是
	 */
	private Integer isLeaf;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 删除状态 0正常 1已删除
	 */
	private String isDeleted;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新人
	 */
	private String updateBy;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**alwaysShow*/
    private boolean alwaysShow;
    /**是否隐藏路由菜单: 0否,1是（默认值0）*/
    private boolean hidden;


	public SysPermissionTree() {
	}

	public SysPermissionTree(SysPermission permission) {
		this.key = permission.getId();
		this.id = permission.getId();
		this.perms = permission.getPerms();
		this.component = permission.getComponent();
		this.createBy = permission.getCreateBy();
		this.createTime = permission.getCreateTime();
		this.isDeleted = permission.getIsDeleted();
		this.description = permission.getDescription();
		this.icon = permission.getIcon();
		this.isLeaf = permission.getIsLeaf();
		this.menuType = permission.getMenuType();
		this.name = permission.getName();
		this.parentId = permission.getParentId();
		this.sortNo = permission.getSortNo();
		this.updateBy = permission.getUpdateBy();
		this.updateTime = permission.getUpdateTime();
		this.redirect = permission.getRedirect();
		this.url = permission.getUrl();
		this.hidden = permission.isHidden();
		this.alwaysShow= permission.isAlwaysShow();
		if (permission.getIsLeaf() == 0) {
			this.children = new ArrayList<SysPermissionTree>();
		}
	}

	private List<SysPermissionTree> children;

	public boolean isAlwaysShow() {
		return alwaysShow;
	}

	public void setAlwaysShow(boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
	}
	public List<SysPermissionTree> getChildren() {
		return children;
	}

	public void setChildren(List<SysPermissionTree> children) {
		this.children = children;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

}
