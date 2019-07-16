package com.zgs.modules.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zgs.common.util.IdGen;
import com.zgs.modules.system.entity.SysDept;
import com.zgs.modules.system.mapper.SysDeptMapper;
import com.zgs.modules.system.model.SysDeptTreeModel;
import com.zgs.modules.system.service.ISysDeptService;
import com.zgs.common.util.YouBianCodeUtil;
import com.zgs.modules.system.controller.FindDeptChildrenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.netty.util.internal.StringUtil;

/**
 * 部门表 服务实现类
 */
@Service
public class SysDeptServiceImpl<T> extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

	/**
	 * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
	 */
	@Override
	public List<SysDeptTreeModel> queryTreeList() {
		LambdaQueryWrapper<SysDept> query = new LambdaQueryWrapper<SysDept>();
		query.eq(SysDept::getIsDeleted, 0);
		query.orderByAsc(SysDept::getDepartOrder);
		List<SysDept> list = this.list(query);
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<SysDeptTreeModel> listResult = FindDeptChildrenUtil.wrapTreeDataToTreeList(list);
		return listResult;
	}

	/**
	 * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
	 */
	@Override
	@Transactional
	public void saveDeptData(SysDept sysDept, String userId) {

		if (sysDept != null && userId != null) {
			if (sysDept.getParentId() == null) {
				sysDept.setParentId("");
			}

			String deptId = IdGen.uuid();
			sysDept.setId(deptId);

			// 先判断该对象有无父级ID,有则意味着不是最高级,否则意味着是最高级
			// 获取父级ID
			String parentId = sysDept.getParentId();
			String[] codeArray = generateOrgCode(parentId);
			sysDept.setOrgCode(codeArray[0]);
			String orgType = codeArray[1];
			sysDept.setOrgType(String.valueOf(orgType));
			sysDept.setCreateTime(new Date());
			sysDept.setCreateBy(userId);
			sysDept.setIsDeleted("0");

			this.save(sysDept);
		}
	}

	/**
	 * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
	 */
	@Override
	@Transactional
	public void updateDeptDataById(SysDept sysDept, String userId) {

		if (sysDept != null && userId != null) {
			sysDept.setUpdateTime(new Date());
			sysDept.setUpdateBy(userId);
			this.updateById(sysDept);
		}
	}
	
	/**
	 * saveDepartData 的调用方法,生成部门编码和部门类型
	 * 
	 * @param parentId
	 * @return
	 */
	private String[] generateOrgCode(String parentId) {

		LambdaQueryWrapper<SysDept> query = new LambdaQueryWrapper<SysDept>();
		LambdaQueryWrapper<SysDept> query1 = new LambdaQueryWrapper<SysDept>();
		String[] strArray = new String[2];
		// 创建一个List集合,存储查询返回的所有SysDepart对象
		List<SysDept> departList = new ArrayList<>();
		// 定义新编码字符串
		String newOrgCode = "";
		// 定义旧编码字符串
		String oldOrgCode = "";
		// 定义部门类型
		String orgType = "";
		// 如果是最高级,则查询出同级的org_code, 调用工具类生成编码并返回
		if (StringUtil.isNullOrEmpty(parentId)) {
			// 线判断数据库中的表是否为空,空则直接返回初始编码
			query1.eq(SysDept::getParentId, "");
			query1.orderByDesc(SysDept::getOrgCode);
			departList = this.list(query1);
			if (departList == null || departList.size() == 0) {
				strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
				strArray[1] = "1";
				return strArray;
			} else {
				SysDept depart = departList.get(0);
				oldOrgCode = depart.getOrgCode();
				orgType = depart.getOrgType();
				newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
			}
		} else { // 反之则查询出所有同级的部门,获取结果后有两种情况,有同级和没有同级
			// 封装查询同级的条件
			query.eq(SysDept::getParentId, parentId);
			// 降序排序
			query.orderByDesc(SysDept::getOrgCode);
			// 查询出同级部门的集合
			List<SysDept> parentList = this.list(query);
			// 查询出父级部门
			SysDept depart = this.getById(parentId);
			// 获取父级部门的Code
			String parentCode = depart.getOrgCode();
			// 根据父级部门类型算出当前部门的类型
			orgType = String.valueOf(Integer.valueOf(depart.getOrgType()) + 1);
			// 处理同级部门为null的情况
			if (parentList == null || parentList.size() == 0) {
				// 直接生成当前的部门编码并返回
				newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
			} else { //处理有同级部门的情况
				// 获取同级部门的编码,利用工具类
				String subCode = parentList.get(0).getOrgCode();
				// 返回生成的当前部门编码
				newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
			}
		}
		// 返回最终封装了部门编码和部门类型的数组
		strArray[0] = newOrgCode;
		strArray[1] = orgType;
		return strArray;
    }

	/**
	 * <p>
	 * 根据关键字搜索相关的部门数据
	 * </p>
	 */
	@Override
	public List<SysDeptTreeModel> searchBy(String keyWord) {

		LambdaQueryWrapper<SysDept> query = new LambdaQueryWrapper<SysDept>();
		query.like(SysDept::getDepartName, keyWord);

		List<SysDept> departList = this.list(query);
		List<SysDeptTreeModel> newList = new ArrayList<>();
		if (departList.size() > 0) {

			for(SysDept depart : departList) {
				newList.add(new SysDeptTreeModel(depart));
			}
			return newList;
		}
		return null;
	}
}
