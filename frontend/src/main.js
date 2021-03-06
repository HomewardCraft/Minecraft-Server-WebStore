//引入Vue
import Vue from 'vue'
//引入app组件
import App from './App.vue'
//引入插件
import developerplugin from "./components/plugins/developer";
//引入vuex
//引入store
import store from './store/index.js'
//引入
import VueRouter from 'vue-router'

import router from "@/router";

//引入爱了门特
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 引入axios并将其添加到全局对象
import axios from "axios";

import './index.css'

axios.defaults.baseURL = 'http://web.fantang.cc:50012/'
Vue.prototype.$http = axios

// 定义价格的过滤器
Vue.filter("priceFormat",(price) => {
  return (price / 100).toFixed(2)
})

//使用插件
Vue.use(developerplugin)
Vue.use(VueRouter)
Vue.use(ElementUI)

//关闭vue生产提示
Vue.config.productionTip = false



//创建vm
new Vue({
  store,
  router,
  beforeCreate() {
    Vue.prototype.$bus = this //全局事件总线
  },
  render: h => h(App)
}).$mount('#app')