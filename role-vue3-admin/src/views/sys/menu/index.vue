<template>
  <div class="app-container">
    <!-- 操作栏 -->
    <el-row class="header">
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
    </el-row>

    <!-- 菜单表格 -->
    <el-table
        :data="tableData"
        row-key="id"
        stripe
        style="width: 100%; margin-bottom: 20px"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="菜单名称" width="200" />
      <el-table-column prop="icon" label="图标" width="70" align="center">
        <template v-slot="scope">
          <el-icon><svg-icon :icon="scope.row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="70" align="center" />
      <el-table-column prop="perms" label="权限标识" width="200" />
      <el-table-column prop="path" label="组件路径" width="180" />
      <el-table-column prop="menuType" label="菜单类型" width="120" align="center">
        <template v-slot="scope">
          <el-tag size="small" v-if="scope.row.menuType === 'M'" type="danger" effect="dark">目录</el-tag>
          <el-tag size="small" v-else-if="scope.row.menuType === 'C'" type="success" effect="dark">菜单</el-tag>
          <el-tag size="small" v-else-if="scope.row.menuType === 'F'" type="warning" effect="dark">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" />
      <el-table-column prop="action" label="操作" width="400" fixed="right" align="center">
        <template v-slot="scope">
          <el-button type="primary" :icon="Edit" @click="handleDialogValue(scope.row.id)" />
          <el-popconfirm title="您确定要删除这条记录吗？" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="danger" :icon="Delete" />
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框组件 -->
    <Dialog
        :model-value="dialogVisible"
        @update:modelValue="dialogVisible = $event"
        :id="id"
        :dialog-title="dialogTitle"
        :table-data="tableData"
        @initMenuList="initMenuList"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import requestUtil from '@/util/request';
import { DocumentAdd, Delete, Edit } from '@element-plus/icons-vue';
import Dialog from './components/dialog.vue';
import { ElMessage } from 'element-plus';

// 表格数据
const tableData = ref([]);

// 对话框状态
const dialogVisible = ref(false);
const dialogTitle = ref('');
const id = ref(-1);

// 初始化菜单列表
const initMenuList = async () => {
  const res = await requestUtil.get('sys/menu/treeList');
  tableData.value = res.data.treeMenu || []; // 确保是数组
};

// 打开对话框
const handleDialogValue = (menuId) => {
  if (menuId) {
    id.value = menuId;
    dialogTitle.value = '菜单修改';
  } else {
    id.value = -1;
    dialogTitle.value = '菜单添加';
  }
  dialogVisible.value = true;
};

// 删除菜单
const handleDelete = async (id) => {
  const res = await requestUtil.get('sys/menu/delete/' + id);
  if (res.data.code === 200) {
    ElMessage.success('执行成功！');
    initMenuList();
  } else {
    ElMessage.error(res.data.msg);
  }
};

// 初始化加载菜单列表
initMenuList();
</script>

<style lang="scss" scoped>
.header {
  padding-bottom: 16px;
  box-sizing: border-box;
}

::v-deep th.el-table__cell {
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