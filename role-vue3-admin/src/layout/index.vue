<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside width="200px" class="sidebar-container"><Menu/></el-aside>
      <el-container>
        <el-header><Header/></el-header>
        <el-main>
          <Tabs/>
          <router-view/>
        </el-main>
        <el-footer><Footer/></el-footer>
      </el-container>
    </el-container>
  </div>
</template>


<script setup>
import Menu from '@/layout/menu'
import Header from '@/layout/header'
import Footer from '@/layout/footer'
import Tabs from '@/layout/tabs'
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
}
.app-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.sidebar-container {
  background-color: #2d3a4b;
  height: 100%;
}


:deep(ul.el-menu){
  border-right-width: 0px
}

</style>