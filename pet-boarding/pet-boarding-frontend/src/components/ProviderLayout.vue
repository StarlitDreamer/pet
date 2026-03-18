<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <span class="logo-text">宠物寄养管理系统 - 服务商</span>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand" trigger="click">
          <span class="user-dropdown" tabindex="0">
            <el-icon><UserFilled /></el-icon>
            <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username || '服务商' }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="layout-aside">
        <el-menu :default-active="activeMenu" router class="aside-menu">
          <el-menu-item index="/provider/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/provider/shop">
            <el-icon><Shop /></el-icon>
            <span>店铺管理</span>
          </el-menu-item>
          <el-menu-item index="/provider/services">
            <el-icon><Goods /></el-icon>
            <span>服务项目</span>
          </el-menu-item>
          <el-menu-item index="/provider/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/provider/care">
            <el-icon><FirstAidKit /></el-icon>
            <span>照料记录</span>
          </el-menu-item>
          <el-menu-item index="/provider/reviews">
            <el-icon><Star /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="/provider/complaints">
            <el-icon><Warning /></el-icon>
            <span>投诉处理</span>
          </el-menu-item>
          <el-menu-item index="/provider/employees">
            <el-icon><Avatar /></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/provider/attendance">
            <el-icon><Clock /></el-icon>
            <span>考勤管理</span>
          </el-menu-item>
          <el-menu-item index="/provider/marketing">
            <el-icon><Present /></el-icon>
            <span>营销活动</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/provider/shop')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #409eff;
  color: #fff;
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
}
.logo-text {
  font-size: 18px;
  font-weight: bold;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  outline: none;
}
.layout-aside {
  background: #fff;
  border-right: 1px solid #e6e6e6;
}
.aside-menu {
  border-right: none;
  height: 100%;
}
.layout-main {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
</style>
