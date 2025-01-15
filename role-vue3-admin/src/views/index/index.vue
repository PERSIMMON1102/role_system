<template>
  <div class="home">
    欢迎使用，admin 通用权限系统 ！
  </div>
</template>

<script>
export default {

  name: "index"
};
</script>

<style lang="scss" scoped>
.home {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh; // 使内容垂直居中
  padding: 40px;
  font-size: 30px;
  font-weight: bold;
  color: #409EFF; // 文字颜色（浅蓝色）
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1); // 文字阴影
  animation: fadeIn 1.5s ease-in-out; // 渐显动画
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>