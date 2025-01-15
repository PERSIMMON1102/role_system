<template>
  <div class="navbar">
    <!-- 面包屑导航 -->
    <Breadcrumb />

    <!-- 右侧区域 -->
    <div class="navbar-right">
      <!-- 头像 -->
      <Avatar />
    </div>
  </div>
</template>

<script setup>
import Breadcrumb from './components/breadcrumb.vue';
import Avatar from './components/avatar.vue';
</script>

<style lang="scss" scoped>
.navbar {
  width: 100%;
  height: 80px; // 增加高度
  background-color: #ffffff; // 使用白色背景
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); // 更柔和的阴影
  padding: 0 32px; // 增加左右内边距
  display: flex;
  align-items: center;
  box-sizing: border-box;
  position: relative;
  transition: all 0.3s ease; // 添加过渡效果

  // 右侧区域
  .navbar-right {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    // 导航栏项
    :deep(.navbar-item) {
      display: inline-block;
      margin-left: 24px; // 增加间距
      font-size: 18px; // 调整字体大小
      color: #606266; // 使用更柔和的文字颜色
      box-sizing: border-box;
      cursor: pointer;
      transition: color 0.3s ease; // 添加文字颜色过渡效果

      &:hover {
        color: #409EFF; // 鼠标悬停时文字颜色变为浅蓝色
      }
    }

    // 头像
    :deep(.avatar) {
      width: 48px; // 增加头像大小
      height: 48px; // 增加头像大小
      border-radius: 50%; // 圆形头像
      overflow: hidden;
      cursor: pointer;
      transition: transform 0.3s ease, box-shadow 0.3s ease; // 添加过渡效果

      &:hover {
        transform: scale(1.1); // 鼠标悬停时放大
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2); // 添加阴影
      }
    }
  }
}
</style>