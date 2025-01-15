<template>
  <div class="app-container">
    <el-row :gutter="20" class="row-container">
      <el-col :span="8">
        <el-card class="box-card">
          <template v-slot:header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <avatar :user="currentUser" />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon="user" />&nbsp;&nbsp;用户名称
                <div class="pull-right">{{ currentUser.username }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon="phone" />&nbsp;&nbsp;手机号码
                <div class="pull-right">{{ currentUser.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon="email" />&nbsp;&nbsp;用户邮箱
                <div class="pull-right">{{ currentUser.email }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon="peoples" />&nbsp;&nbsp;所属角色
                <div class="pull-right">{{ currentUser.roles }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon="date" />&nbsp;&nbsp;创建日期
                <div class="pull-right">{{ formatDate(currentUser.loginDate) }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16" class="col-container">
        <el-card class="box-card">
          <template v-slot:header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          <div class="card-content">
            <el-collapse v-model="activeCollapse" class="custom-collapse">
              <el-collapse-item title="基本资料" name="userinfo">
                <el-form ref="userRef" :model="form" :rules="rules" label-width="100px" class="custom-form">
                  <el-form-item label="手机号码：" prop="phonenumber">
                    <el-input v-model="form.phonenumber" maxlength="11" class="custom-input" />
                  </el-form-item>
                  <el-form-item label="用户邮箱：" prop="email">
                    <el-input v-model="form.email" maxlength="50" class="custom-input" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleSubmit" class="custom-button">保存</el-button>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
              <el-collapse-item title="修改密码" name="resetPwd">
                <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-width="80px" class="custom-form">
                  <el-form-item label="旧密码" prop="oldPassword">
                    <el-input v-model="pwdForm.oldPassword" placeholder="请输入旧密码" type="password" show-password class="custom-input" />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="pwdForm.newPassword" placeholder="请输入新密码" type="password" show-password class="custom-input" />
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="pwdForm.confirmPassword" placeholder="请确认密码" type="password" show-password class="custom-input" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handlePwdSubmit" class="custom-button">保存</el-button>
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import avatar from './avatar'
import { ElMessage } from 'element-plus'
import requestUtil from '@/util/request'
import store from '@/store'
import { formatDate } from '@/util/formatDate'

const currentUser = ref(store.getters.GET_USERINFO)
const activeCollapse = ref(['userinfo']) // 默认展开的折叠项

const userRef = ref(null)
const pwdRef = ref(null)

const form = ref({
  id: currentUser.value.id,
  phonenumber: currentUser.value.phonenumber,
  email: currentUser.value.email
})

const pwdForm = ref({
  id: currentUser.value.id,
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = ref({
  email: [
    { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { required: true, message: '手机号码不能为空', trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

const equalToPassword = (rule, value, callback) => {
  if (pwdForm.value.newPassword !== value) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = ref({
  oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    { required: true, validator: equalToPassword, trigger: 'blur' }
  ]
})

const handleSubmit = () => {
  userRef.value.validate(async (valid) => {
    if (valid) {
      let result = await requestUtil.post('sys/user/save', form.value)
      let data = result.data
      if (data.code === 200) {
        ElMessage.success('执行成功！')
        store.commit('SET_USERINFO', form.value)
      }
    }
  })
}

const handlePwdSubmit = () => {
  pwdRef.value.validate(async (valid) => {
    if (valid) {
      let result = await requestUtil.post('sys/user/updateUserPwd', pwdForm.value)
      let data = result.data
      if (data.code === 200) {
        ElMessage.success('密码修改成功，下一次登录生效！')
        store.commit('SET_USERINFO', pwdForm.value)
      } else {
        ElMessage.error(data.msg)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.app-container {
  height: 70vh; /* 设置容器高度为视口高度 */
  padding: 20px;
  overflow: hidden; /* 禁用滚动 */
}

.row-container {
  height: 100%;
}

.col-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.box-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-content {
  flex: 1;
  padding: 10px;
  overflow: hidden; /* 禁用内容滚动 */
}

/* 折叠面板标题样式 */
.custom-collapse {
  :deep(.el-collapse-item__header) {
    font-size: 16px; /* 标题字体大小 */
    font-weight: bold; /* 标题字体加粗 */
    color: #333; /* 标题字体颜色 */
  }
}

/* 表单样式 */
.custom-form {
  :deep(.el-form-item__label) {
    font-size: 14px; /* 标签字体大小 */
    color: #666; /* 标签字体颜色 */
  }

  :deep(.el-input__inner) {
    font-size: 14px; /* 输入框字体大小 */
    color: #333; /* 输入框字体颜色 */
  }
}

/* 按钮样式 */
.custom-button {
  font-size: 14px; /* 按钮字体大小 */
  padding: 10px 20px; /* 按钮内边距 */
}

.list-group-striped > .list-group-item {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}

.list-group-item {
  border-bottom: 1px solid #e7eaec;
  border-top: 1px solid #e7eaec;
  margin-bottom: -1px;
  padding: 11px 0;
  font-size: 13px;
}

.pull-right {
  float: right !important;
}
</style>