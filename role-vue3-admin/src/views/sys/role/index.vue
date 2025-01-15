<template>
  <div class="app-container">
    <!-- 搜索和操作栏 -->
    <el-row :gutter="20" class="header">
      <el-col :span="7">
        <el-input placeholder="请输入角色名..." v-model="queryForm.query" clearable></el-input>
      </el-col>
      <el-button type="primary" :icon="Search" @click="initRoleList">搜索</el-button>
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
      <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
        <template #reference>
          <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
        </template>
      </el-popconfirm>
    </el-row>

    <!-- 角色列表表格 -->
    <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="角色名" width="100" align="center" />
      <el-table-column prop="code" label="权限字符" width="200" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="200" align="center" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="action" label="操作" width="400" fixed="right" align="center">
        <template v-slot="scope">
          <el-button type="primary" :icon="Tools" @click="handleMenuDialogValue(scope.row.id)">分配权限</el-button>
          <el-button v-if="scope.row.code != 'admin'" type="primary" :icon="Edit" @click="handleDialogValue(scope.row.id)" />
          <el-popconfirm v-if="scope.row.code != 'admin'" title="您确定要删除这条记录吗？" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="danger" :icon="Delete" />
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        v-model:currentPage="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 角色编辑对话框 -->
    <Dialog
        :model-value="dialogVisible"
        @update:modelValue="dialogVisible = $event"
        :id="id"
        :dialogTitle="dialogTitle"
        @initRoleList="initRoleList"
    />

    <!-- 分配权限对话框 -->
    <el-dialog
        :model-value="menuDialogVisible"
        title="分配权限"
        width="30%"
        top="25px"
        @update:modelValue="handleMenuDialogClose"
    >
      <el-form ref="formRef" :model="form">
        <el-tree
            ref="treeRef"
            :data="treeData"
            :props="defaultProps"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :check-strictly="true"
        />
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="handleMenuDialogConfirm">确认</el-button>
          <el-button @click="handleMenuDialogClose">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import requestUtil, { getServerUrl } from '@/util/request';
import { Search, Delete, DocumentAdd, Edit, Tools } from '@element-plus/icons-vue';
import Dialog from './components/dialog.vue';
import { ElMessage } from 'element-plus';

// 角色列表数据
const tableData = ref([]);
const total = ref(0);
const queryForm = ref({
  query: '',
  pageNum: 1,
  pageSize: 10,
});

// 对话框状态
const dialogVisible = ref(false);
const dialogTitle = ref('');
const id = ref(-1);

// 批量删除按钮状态
const delBtnStatus = ref(true);
const multipleSelection = ref([]);

// 分配权限对话框状态
const menuDialogVisible = ref(false);
const treeRef = ref(null);
const treeData = ref([]);
const form = ref({ id: -1 });

// 树形控件配置
const defaultProps = {
  children: 'children',
  label: 'name',
};

// 初始化角色列表
const initRoleList = async () => {
  const res = await requestUtil.post('sys/role/list', queryForm.value);
  tableData.value = res.data.roleList;
  total.value = res.data.total;
};

// 分页大小变化
const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initRoleList();
};

// 当前页码变化
const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initRoleList();
};

// 打开角色编辑对话框
const handleDialogValue = (roleId) => {
  if (roleId) {
    id.value = roleId;
    dialogTitle.value = '角色修改';
  } else {
    id.value = -1;
    dialogTitle.value = '角色添加';
  }
  dialogVisible.value = true;
};

// 打开分配权限对话框
const handleMenuDialogValue = async (roleId) => {
  id.value = roleId;
  menuDialogVisible.value = true;

  // 初始化菜单树数据
  const res = await requestUtil.get('sys/menu/treeList');
  treeData.value = res.data.treeMenu;

  // 设置已选中的菜单
  const res2 = await requestUtil.get('sys/role/menus/' + roleId);
  treeRef.value.setCheckedKeys(res2.data.menuIdList);
};

// 关闭分配权限对话框
const handleMenuDialogClose = () => {
  menuDialogVisible.value = false;
};

// 确认分配权限
const handleMenuDialogConfirm = async () => {
  const menuIds = treeRef.value.getCheckedKeys();
  const result = await requestUtil.post('sys/role/updateMenus/' + id.value, menuIds);
  if (result.data.code === 200) {
    ElMessage.success('执行成功！');
    initRoleList();
    handleMenuDialogClose();
  } else {
    ElMessage.error(result.data.msg);
  }
};

// 删除角色
const handleDelete = async (id) => {
  const ids = id ? [id] : multipleSelection.value.map((row) => row.id);
  const res = await requestUtil.post('sys/role/delete', ids);
  if (res.data.code == 200) {
    ElMessage.success('执行成功！');
    initRoleList();
  } else {
    ElMessage.error(res.data.msg);
  }
};

// 表格选中事件
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
  delBtnStatus.value = selection.length === 0;
};

// 初始化加载角色列表
initRoleList();
</script>

<style lang="scss" scoped>
.header {
  padding-bottom: 16px;
  box-sizing: border-box;
}

.el-pagination {
  float: right;
  padding: 20px;
  box-sizing: border-box;
}

:deep(th.el-table__cell)  {
  word-break: break-word;
  background-color: #f8f8f9 !important;
  color: #515a6e;
  height: 40px;
  font-size: 13px;
}

.el-tag--small {
  margin-left: 5px;
}
</style>