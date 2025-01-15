import { createStore } from 'vuex';
import router from '@/router';

// 用户模块
const userModule = {
  state: () => ({
    token: sessionStorage.getItem('token') || '', // 从 sessionStorage 初始化 token
    menuList: JSON.parse(sessionStorage.getItem('menuList')) || [], // 从 sessionStorage 初始化菜单列表
    userInfo: JSON.parse(sessionStorage.getItem('userInfo')) || null, // 从 sessionStorage 初始化用户信息
    hasRoutes: sessionStorage.getItem('hasRoutes') === 'true' || false,// 从 sessionStorage 初始化路由状态
  }),
  getters: {
    GET_TOKEN: (state) => state.token, // 获取 token
    GET_MENULIST: (state) => state.menuList, // 获取菜单列表
    GET_USERINFO: (state) => state.userInfo, // 获取用户信息
    GET_HASROUTES: (state) => state.hasRoutes // 获取路由状态
  },
  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token;
      sessionStorage.setItem('token', token); // 更新 token 并同步到 sessionStorage
    },
    SET_MENULIST: (state, menuList) => {
      // 如果 menuList 是 JSON 字符串，则解析为数组
      const parsedMenuList = typeof menuList === 'string' ? JSON.parse(menuList) : menuList;
      state.menuList = parsedMenuList;
      sessionStorage.setItem('menuList', JSON.stringify(parsedMenuList)); // 更新菜单列表并同步到 sessionStorage
    },
    SET_USERINFO: (state, userInfo) => {
      state.userInfo = userInfo;
      sessionStorage.setItem('userInfo', JSON.stringify(userInfo)); // 更新用户信息并同步到 sessionStorage
    },
    SET_ROUTES_STATE: (state, hasRoutes) => {
      state.hasRoutes = hasRoutes;
      sessionStorage.setItem('hasRoutes', hasRoutes); // 更新路由状态并同步到 sessionStorage
    }
  },
  actions: {
    // 安全退出
    logout({ commit }) {
      commit('SET_TOKEN', ''); // 清空 token
      commit('SET_MENULIST', []); // 清空菜单列表
      commit('SET_USERINFO', null); // 清空用户信息
      commit('SET_ROUTES_STATE', false); // 重置路由状态
      sessionStorage.clear(); // 清空 sessionStorage
      router.replace('/login'); // 跳转到登录页面
    }
  }
};

// 标签页模块
const tabsModule = {
  state: () => ({
    editableTabsValue: '/index', // 当前选中的标签页
    editableTabs: [
      {
        title: '首页',
        name: '/index'
      }
    ]
  }),
  getters: {
    GET_EDITABLE_TABS: (state) => state.editableTabs, // 获取标签页列表
    GET_ACTIVE_TAB: (state) => state.editableTabsValue // 获取当前选中的标签页
  },
  mutations: {
    ADD_TABS: (state, tab) => {
      if (state.editableTabs.findIndex((e) => e.name === tab.path) === -1) {
        state.editableTabs.push({
          title: tab.name,
          name: tab.path,
        });
      }
      state.editableTabsValue = tab.path;
    },
    REMOVE_TAB: (state, tabName) => {
      // 移除标签页
      const tabs = state.editableTabs;
      const activeTab = state.editableTabsValue;

      if (activeTab === tabName) {
        // 如果移除的是当前选中的标签页，则切换到前一个标签页
        const index = tabs.findIndex((tab) => tab.name === tabName);
        state.editableTabsValue = tabs[index - 1]?.name || '/index';
      }

      state.editableTabs = tabs.filter((tab) => tab.name !== tabName);
    },
    RESET_TABS: (state) => {
      // 重置标签页状态
      state.editableTabsValue = '/index';
      state.editableTabs = [
        {
          title: '首页',
          name: '/index'
        }
      ];
    },
    SET_ACTIVE_TAB: (state, tabName) => {
      // 设置当前选中的标签页
      state.editableTabsValue = tabName;
    }
  },
  actions: {
    // 添加标签页
    addTab({ commit }, tab) {
      commit('ADD_TABS', tab);
    },
    // 移除标签页
    removeTab({ commit }, tabName) {
      commit('REMOVE_TAB', tabName);
    },
    // 重置标签页
    resetTabs({ commit }) {
      commit('RESET_TABS');
    }
  }
};

// 创建 Vuex Store
export default createStore({
  modules: {
    user: userModule, // 用户模块
    tabs: tabsModule // 标签页模块
  }
});