<template>
  <router-view/>

<!--  <el-button type="primary" @click="handleLogin">测试登录</el-button>-->
<!--  <el-button type="danger" @click="handleUserList">测试获取用户请求</el-button>-->
</template>

<script setup>
import { ref ,watch} from 'vue'
import { useRoute,useRouter } from 'vue-router'
const route=useRoute();
const router=useRouter();
const whitePath=['/login','/index','/']
import  store from '@/store'
watch(route,(to,from)=>{
  console.log("to"+to.name)
  console.log(to.path)
  if (whitePath.indexOf(to.path)===-1) {
    console.log("to.path="+to.path)
    let obj = {
      name: to.name,
      path: to.path
    }
    store.commit("ADD_TABS", obj)
  }
},{deep:true,immediate:true})





// import requestUtil from '@/util/request'
// import store from '@/store'
//
// const handleLogin = async () => {
//   let result = await requestUtil.get("test/login");
//   let data = result.data;
//
//   // 使用双等号或三等号进行比较
//   if(data.code === 200) {
//     const token = data.token; // 获取 token
//     console.log("登录成功：token=" + token);
//     store.commit('SET_TOKEN', token);
//   } else {
//     console.log("登录出错！");
//   }
// }
//
// const handleUserList = async () => {
//   const token = store.getters.GET_TOKEN;; // 从 Vuex 中获取 token
//
//   console.log("当前 token:", token); // 检查 token
//
//   if (!token) {
//     console.log("错误：没有权限访问，请先登录。");
//     return; // 提前返回
//   }
//
//   let result = await requestUtil.get("test/user/list", {
//     headers: {
//       'Authorization': `Bearer ${token}` // 确保 token 被正确传递
//     }
//   });
//
//   let data = result.data;
//
//   if (data.code === 200) {
//     const userList = data.userList; // 确保访问 userList
//     console.log("用户列表信息：userList=", userList);
//   } else {
//     console.log("获取用户列表失败，错误代码：" + data.code);
//   }
// }
</script>

<style>
html,body,#app{
  height: 100%;
}

.app-container{
  padding:20px
}
</style>