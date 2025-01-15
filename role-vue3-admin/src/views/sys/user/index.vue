<template>
  <div class="app-container">
    <el-row :gutter="20" class="header">
      <el-col :span="7">
        <el-input placeholder="请输入用户名..." v-model="queryForm.query" clearable></el-input>
      </el-col>
      <el-button type="primary" :icon="Search" @click="initUserList">搜索</el-button>
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
      <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
        <template #reference>
          <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
        </template>
      </el-popconfirm>
    </el-row>

    <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="avatar" label="头像" width="80" align="center">
        <template v-slot="scope">
          <img :src="getServerUrl() + 'image/userAvatar/' + scope.row.avatar" width="50" height="50" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="100" align="center" />
      <el-table-column prop="roles" label="拥有角色" width="200" align="center">
        <template v-slot="scope">
          <el-tag size="small" type="warning" v-for="item in scope.row.sysRoleList">{{ item.name }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="200" align="center" />
      <el-table-column prop="phonenumber" label="手机号" width="120" align="center" />
      <el-table-column prop="status" label="状态？" width="200" align="center">
        <template v-slot="{ row }">
          <el-switch v-model="row.status" @change="statusChangeHandle(row)" active-text="正常" inactive-text="禁用"
                     active-value="0" inactive-value="1"></el-switch>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="200" align="center" />
      <el-table-column prop="loginDate" label="最后登录时间" width="200" align="center" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="action" label="操作" width="400" fixed="right" align="center">
        <template v-slot="scope">
          <el-button type="primary" :icon="Tools"
                     @click="handleRoleDialogValue(scope.row.id, scope.row.sysRoleList)">分配角色</el-button>
          <el-button v-if="scope.row.username != 'java1234'" type="primary" :icon="Edit"
                     @click="handleDialogValue(scope.row.id)" />
          <el-popconfirm v-if="scope.row.username != 'java1234'" title="您确定要删除这条记录吗？"
                         @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="danger" :icon="Delete" />
            </template>
          </el-popconfirm>
          <el-popconfirm v-if="scope.row.username != 'java1234'" title="您确定要对这个用户重置密码吗？"
                         @confirm="handleResetPassword(scope.row.id)">
            <template #reference>
              <el-button type="warning" :icon="RefreshRight">重置密码</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:currentPage="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
                   :page-sizes="[10, 20, 30, 40]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                   @size-change="handleSizeChange" @current-change="handleCurrentChange" />

    <Dialog v-model="dialogVisible" :id="id" :dialogTitle="dialogTitle" @initUserList="initUserList" />

    <!-- RoleDialog 组件 -->
    <el-dialog
        :model-value="roleDialogVisible"
        title="分配角色"
        width="30%"
        @close="handleCloseRoleDialog"
        @update:model-value="handleUpdateRoleDialog"
    >
      <el-form
          ref="roleFormRef"
          :model="roleForm"
          label-width="100px"
      >
        <el-checkbox-group v-model="roleForm.checkedRoles">
          <el-checkbox v-for="role in roleForm.roleList" :id="role.id" :key="role.id" :label="role.id" name="checkedRoles">{{role.name}}</el-checkbox>
        </el-checkbox-group>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="handleConfirmRoleDialog">确认</el-button>
          <el-button @click="handleCloseRoleDialog">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import requestUtil, { getServerUrl } from "@/util/request";
import { Search, Delete, DocumentAdd, Edit, Tools, RefreshRight } from '@element-plus/icons-vue';
import Dialog from './components/dialog.vue';
import { ElMessage } from 'element-plus';

// 用户列表相关逻辑
const tableData = ref([]);
const total = ref(0);
const queryForm = ref({
  query: '',
  pageNum: 1,
  pageSize: 10,
});

const dialogVisible = ref(false);
const dialogTitle = ref("");
const id = ref(-1);
const delBtnStatus = ref(true);
const multipleSelection = ref([]);
const roleDialogVisible = ref(false);

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
  delBtnStatus.value = selection.length == 0;
};

const initUserList = async () => {
  try {
    const res = await requestUtil.post("sys/user/list", queryForm.value);
    if (res.data.code === 200 && Array.isArray(res.data.userList)) {
      tableData.value = res.data.userList;
      total.value = res.data.total;
    } else {
      console.error('API 返回的数据格式错误:', res.data);
      tableData.value = [];
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
};
initUserList();

const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initUserList();
};

const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initUserList();
};

const handleDialogValue = (userId) => {
  if (userId) {
    id.value = userId;
    dialogTitle.value = "用户修改";
  } else {
    id.value = -1;
    dialogTitle.value = "用户添加";
  }
  dialogVisible.value = true;
};

const handleDelete = async (id) => {
  var ids = [];
  if (id) {
    ids.push(id);
  } else {
    multipleSelection.value.forEach(row => {
      ids.push(row.id);
    });
  }
  const res = await requestUtil.post("sys/user/delete", ids);
  if (res.data.code == 200) {
    ElMessage.success('执行成功!');
    initUserList();
  } else {
    ElMessage.error(res.data.msg);
  }
};

const handleResetPassword = async (id) => {
  const res = await requestUtil.get("sys/user/resetPassword/" + id);
  if (res.data.code == 200) {
    ElMessage.success('执行成功!');
    initUserList();
  } else {
    ElMessage.error(res.data.msg);
  }
};

const statusChangeHandle = async (row) => {
  let res = await requestUtil.get("sys/user/updateStatus/" + row.id + "/status/" + row.status);
  if (res.data.code == 200) {
    ElMessage.success('执行成功!');
  } else {
    ElMessage.error(res.data.msg);
    initUserList();
  }
};

// RoleDialog 相关逻辑
const roleForm = ref({
  id: -1,
  roleList: [],
  checkedRoles: []
});

const roleFormRef = ref(null);

const handleRoleDialogValue = (userId, roleList) => {
  roleForm.value.id = userId;
  roleForm.value.checkedRoles = roleList.map(role => role.id);
  roleDialogVisible.value = true;
  initRoleList();
};

const initRoleList = async () => {
  const res = await requestUtil.get("sys/role/listAll");
  roleForm.value.roleList = res.data.roleList;
};

const handleCloseRoleDialog = () => {
  roleDialogVisible.value = false;
};

const handleUpdateRoleDialog = (value) => {
  roleDialogVisible.value = value;
};

const handleConfirmRoleDialog = () => {
  roleFormRef.value.validate(async (valid) => {
    if (valid) {
      const res = await requestUtil.post("sys/user/grantRole/" + roleForm.value.id, roleForm.value.checkedRoles);
      if (res.data.code == 200) {
        ElMessage.success("执行成功！");
        initUserList();
        handleCloseRoleDialog();
      } else {
        ElMessage.error(res.data.msg);
      }
    } else {
      console.log("表单验证失败");
    }
  });
};
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