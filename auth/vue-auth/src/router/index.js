import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/Login'

Vue.use(Router)

const router = new Router({
  mode:'history',
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    }
  ]
});

router.beforeEach((to, from, next) => {
  let access_token = localStorage.getItem('access_token');
  console.log(access_token);
  if (access_token || to.path === '/login') {//一定要加|| to.path === '/login'，否则引起死循环
    next();
  } else {
    next({
      path: '/login',
      query: {redirect: to.fullPath}//把要跳转的地址作为参数传到下一步，点击登录跳转回来
    })
  }
});

export default router;
