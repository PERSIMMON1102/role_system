<template>
  <div class="footer">
    Copyright © 2012-2099 Tomtian知识分享网 版权所有&nbsp;&nbsp;
    <a href="http://www.Tomtian.vip" target="_blank">www.Tomtian.com</a>
  </div>
</template>

<script setup>
// 无需额外的逻辑
</script>

<style lang="scss" scoped>
.footer {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center; /* 内容居中 */
  background-color: #ffffff; /* 浅色背景 */
  color: #606266; /* 深色文字 */
  font-size: 14px; /* 字体大小 */
}

a {
  color: #409EFF; /* 链接颜色 */
  text-decoration: none; /* 去掉下划线 */
  transition: color 0.3s ease; /* 颜色过渡效果 */

  &:hover {
    color: #337ecc; /* 悬停时颜色 */
    text-decoration: underline; /* 悬停时显示下划线 */
  }
}
</style>