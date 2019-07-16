package com.zgs.modules.system.controller;

import com.zgs.modules.system.entity.SysDept;
import com.zgs.modules.system.model.DepartIdModel;
import com.zgs.modules.system.model.SysDeptTreeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 * 对应部门的表,处理并查找树级数据
 * <P>
 * 
 * @author zgs
 */
public class FindDeptChildrenUtil {

    private static List<DepartIdModel> idList = new ArrayList<>(4);

    /**
     * queryTreeList的子方法 ====1=====
     * 该方法是s将SysDepart类型的list集合转换成SysDepartTreeModel类型的集合
     */
    public static List<SysDeptTreeModel> wrapTreeDataToTreeList(List<SysDept> recordList) {
     // 在该方法每请求一次,都要对全局list集合进行一次清理
        idList.clear();
        List<SysDeptTreeModel> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            SysDept dept = recordList.get(i);
            records.add(new SysDeptTreeModel(dept));
        }
        List<SysDeptTreeModel> tree = findChildren(records, idList);
        setEmptyChildrenAsNull(tree);
        return tree;
    }

    public static List<DepartIdModel> wrapDepartIdModel() {
        return idList;
    }

    /**
     * queryTreeList的子方法 ====2=====
     * 该方法是找到并封装顶级父类的节点到TreeList集合
     */
    private static List<SysDeptTreeModel> findChildren(List<SysDeptTreeModel> recordList,
                                                       List<DepartIdModel> idList) {

        List<SysDeptTreeModel> treeList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            SysDeptTreeModel branch = recordList.get(i);
            if (branch.getParentId().equals("")) {
                treeList.add(branch);
                DepartIdModel departIdModel = new DepartIdModel().convert(branch);
                idList.add(departIdModel);
            }
        }
        getGrandChildren(treeList,recordList,idList);
        return treeList;
    }

    /**
     * queryTreeList的子方法====3====
     *该方法是找到顶级父类下的所有子节点集合并封装到TreeList集合
     */
    private static void getGrandChildren(List<SysDeptTreeModel> treeList, List<SysDeptTreeModel> recordList, List<DepartIdModel> idList) {

        for (int i = 0; i < treeList.size(); i++) {
            SysDeptTreeModel model = treeList.get(i);
            DepartIdModel idModel = idList.get(i);
            for (int i1 = 0; i1 < recordList.size(); i1++) {
                SysDeptTreeModel m = recordList.get(i1);
                if (m.getParentId().equals(model.getId())) {
                    model.getChildren().add(m);
                    DepartIdModel dim = new DepartIdModel().convert(m);
                    idModel.getChildren().add(dim);
                }
            }
            getGrandChildren(treeList.get(i).getChildren(), recordList, idList.get(i).getChildren());
        }

    }
    

    /**
     * queryTreeList的子方法 ====4====
     * 该方法是将子节点为空的List集合设置为Null值
     */
    private static void setEmptyChildrenAsNull(List<SysDeptTreeModel> treeList) {

        for (int i = 0; i < treeList.size(); i++) {
            SysDeptTreeModel model = treeList.get(i);
            if (model.getChildren().size() == 0) {
                model.setChildren(null);
            }else{
                setEmptyChildrenAsNull(model.getChildren());
            }
        }
    }
}
