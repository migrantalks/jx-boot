package com.zgs.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zgs.modules.system.entity.SysDept;
import com.zgs.modules.system.model.SysDeptTreeModel;

import java.util.List;

/**
 * 部门表 服务实现类
 * @author zgs
 */
public interface ISysDeptService extends IService<SysDept>{


    /**
     * 查询所有部门信息,并分节点进行显示
     * @return
     */
    List<SysDeptTreeModel> queryTreeList();

    /**
     * 保存部门数据
     * @param sysDepart
     */
    void saveDeptData(SysDept sysDepart, String userId);

    /**
     * 更新depart数据
     * @param sysDepart
     * @return
     */
    void updateDeptDataById(SysDept sysDepart, String userId);

    
    /**
     * 根据关键字搜索相关的部门数据
     * @param keyWord
     * @return
     */
    List<SysDeptTreeModel> searchBy(String keyWord);
    
}
