<template>
  <el-menu
      active-text-color="#409EFF"
      background-color="#f0f2f5"
      class="el-menu-vertical-demo"
      text-color="#606266"
      router
      :default-active="activeIndex"
  >
    <!-- 首页菜单项 -->
    <el-menu-item index="/index">
      <el-icon><home-filled /></el-icon>
      <span>首页</span>
    </el-menu-item>

    <!-- 动态生成菜单项 -->
    <el-sub-menu :index="menu.path" v-for="menu in menuList" :key="menu.path">
      <template #title>
        <el-icon><svg-icon :icon="menu.icon" /></el-icon>
        <span>{{ menu.name }}</span>
      </template>
      <el-menu-item
          :index="item.path"
          v-for="item in menu.children"
          :key="item.path"
          @click="openTab(item)"
      >
        <el-icon><svg-icon :icon="item.icon" /></el-icon>
        <span>{{ item.name }}</span>
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { HomeFilled } from '@element-plus/icons-vue';
import { ref, watch } from 'vue';
import store from '@/store';

// 获取菜单列表
const menuList = ref(store.getters.GET_MENULIST);

// 打开标签页
const openTab = (item) => {
  store.commit('ADD_TABS', item);
};

const activeIndex = ref("/index");

watch(store.state, () => {
  console.log("editableTabsValue=" + store.state.editableTabsValue);
  activeIndex.value = store.state.editableTabsValue;
}, { deep: true, immediate: true });
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo {
  height: 100%;
  border-right: none;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  overflow-y: auto; /* 允许菜单内容滚动 */

  &::-webkit-scrollbar {
    width: 6px; /* 设置滚动条宽度 */
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.2); /* 滚动条颜色 */
    border-radius: 4px; /* 滚动条圆角 */
  }

  .el-menu-item,
  .el-sub-menu__title {
    padding: 12px 20px;
    margin: 4px 0;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      background-color: rgba(64, 158, 255, 0.1);
    }
  }

  .el-menu-item.is-active {
    background-color: rgba(64, 158, 255, 0.2);
    color: #409EFF;
  }

  .el-icon {
    margin-right: 12px;
    font-size: 18px;
    color: #409EFF;
  }

  .el-sub-menu {
    .el-menu-item {
      padding-left: 50px !important;
    }
  }
}
</style>