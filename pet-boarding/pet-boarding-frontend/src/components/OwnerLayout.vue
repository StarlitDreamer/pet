<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <img src="" alt="" class="logo-icon" style="display:none" />
        <span class="logo-text">宠物寄养管理系统</span>
      </div>
      <div class="header-right">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="msg-badge">
          <el-icon size="20" style="cursor:pointer" @click="$router.push('/owner/profile')"><Bell /></el-icon>
        </el-badge>
        <el-dropdown @command="handleCommand" trigger="click">
          <span class="user-dropdown" tabindex="0">
            <el-icon><UserFilled /></el-icon>
            <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username || '用户' }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="layout-aside">
        <el-menu :default-active="activeMenu" router class="aside-menu">
          <el-menu-item index="/owner/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/owner/pets">
            <el-icon><Connection /></el-icon>
            <span>我的宠物</span>
          </el-menu-item>
          <el-menu-item index="/owner/search">
            <el-icon><Search /></el-icon>
            <span>搜索服务</span>
          </el-menu-item>
          <el-menu-item index="/owner/orders">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/owner/reviews">
            <el-icon><Star /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="/owner/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store'
import { getUnreadCount } from '@/api/message'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const unreadCount = ref(0)

const activeMenu = computed(() => route.path)

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data?.count || 0
  } catch (e) {
    // ignore
  }
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/owner/profile')
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

onMounted(() => {
  fetchUnreadCount()
  setInterval(fetchUnreadCount, 60000)
})
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
.msg-badge {
  margin-top: 4px;
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
