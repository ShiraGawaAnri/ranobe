import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/Home.vue'
import NProgress from 'nprogress'; // progress bar
import 'nprogress/nprogress.css'; // progress bar style
import {
  message
} from 'ant-design-vue';
import {
  getToken,
  setToken,
  removeToken
} from '@/utils/auth';
import store from '@/store';
import NovelContainer from '@/layout/NovelContainer'
import NovelMain from '@/views/novel/Main'

Vue.use(VueRouter)

export const constantRoutes = [
  {
    path: '/',
    name: 'home',
    // component: Home,
    redirect: '/novel/main',
  },
  {
    path: '/user',
    name: 'userpanel',
    component: () => import('@/views/user/Panel'),
    meta: {
      roles: ['ROLE_USER']
    }
  },
  {
    path: '/account',
    name: 'Account',
    component: () => import('@/views/user/Account'),
  },
  {
    path: '/novel',
    name: 'novel',
    component: NovelContainer,
    redirect: '/novel/main',
    children: [{
        path: '/novel/main',
        name: 'Main',
        component: NovelMain,
      },
      {
        path: '/novel/:novelId(\\d+)/details',
        name: 'NovelDetails',
        component: () => import('@/views/novel/Details'),
      },
      {
        path: '/novel/:novelId(\\d+)/episode/:episode(\\d+)/chapter/:chapter(\\d+)',
        name: 'NovelRead',
        component: () => import('@/views/novel/Read'),
      },
      {
        path: '/novel/history',
        name: 'History',
        component: () => import('@/views/novel/History'),
      },
    ],
  },
  {
    path: '/upload',
    name: 'upload',
    component: () => import('@/views/upload/MainUpload'),
    meta: {
      roles: ['ROLE_BROZON_USER']
    }
  },
  {
    path: '/subscribe',
    name: 'Subscribe',
    component: () => import('@/views/novel/Subscribe'),
    meta: {
      //roles: ['ROLE_BROZON_USER']
    }
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true,
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true,
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true,
  },
];
export const asyncRoutes = [
  // {
  //   path: '/upload',
  //   name: 'upload',
  //   component: () => import('@/views/upload/MainUpload'),
  //   meta: {
  //     roles: ['ROLE_BROZON_USER']
  //   }
  // },
  // {
  //   path: '/subscribe',
  //   name: 'Subscribe',
  //   component: () => import('@/views/novel/Subscribe'),
  //   meta: {
  //     roles: ['ROLE_BROZON_USER']
  //   }
  // },
  // {
  //   path: '*',
  //   redirect: '/404',
  //   hidden: true,
  // },

];


const createRouter = () =>
  new VueRouter({
    mode: 'history', // require service support
    // scrollBehavior: () => ({ y: 0 }),
    base: process.env.BASE_URL,
    routes: constantRoutes,
  });

const router = createRouter();
const whiteList = ['/login', '/auth-redirect', '/logout', '/reg', '/401', '/404']; // no redirect whitelist
//加入401防止 远程服务器无响应时跳转401->检测持有token->持有则请求user/getInfo->远程服务器无响应 无限循环
NProgress.configure({
  showSpinner: false,
});

router.beforeEach(async (to, from, next) => {
  NProgress.start();
  const hasToken = getToken();
  const hasRoles = store.getters.roles && store.getters.roles.length > 0;
  let modifitied = false;
  //简单的重定向
  if (to && to.fullPath === "/account?type=login") {
    if (from && from.fullPath && !from.fullPath.match("/account") && !from.fullPath.match("/401") && !from.fullPath.match("/404")) {
      to.query.redirect_uri = from.fullPath;
      modifitied = true;
    }
  }
  if (whiteList.indexOf(to.path) !== -1) {
    next();
    NProgress.done();
  } else if (hasToken) {
    if (hasRoles) {
      next();
      NProgress.done();
    } else {
      try {
        const {
          roles
        } = await store.dispatch('user/getInfo');
        const accessRoutes = await store.dispatch(
          'permission/generateRoutes',
          roles
        );
        router.addRoutes(accessRoutes);
        next({
          ...to,
          replace: true,
        });
        NProgress.done();
      } catch (error) {
        if (error && error.message && !error.message.match("Network Error")) {
          await store.dispatch('user/resetToken');
        }
        message.error(error || 'Has Error');
        next({
          path: '/401',
          replace: true,
          query: {
            noGoBack: true,
          },
        });
        NProgress.done();
      }
    }
  } else {
    let matched = to.matched.some((item) => {
      return Array.isArray(item.meta.roles);
    })
    if (matched) {
      next({
        path: '/401',
        replace: true,
        query: {
          noGoBack: true,
        },
      });
    } else {
      if (modifitied) {
        next({
          path: to.path,
          query: to.query
        });
      } else {
        next();
      }

      NProgress.done();
    }

    //没有权限但是通过输入url直接访问
    // function childrenCheck(array,to){
    //   for(let i = 0;i < array.length;i++){

    //   }
    // }
    // if (constantRoutes.find(each => {
    //     console.log(to);
    //     console.log(each);
    //     if(each.path !== "/"){
    //       if(each.path.indexOf(to.path) !== -1){
    //         return true;
    //       }
    //       if(Array.isArray(each.children)){
    //         if(each.children.find())
    //       }
    //     }
    //     //return to.path === "" || each.meta === undefined || !Array.isArray(each.meta.roles);
    //   })) {
    //   next();
    //   NProgress.done();
    // } else {
    //   next({
    //     path: '/401',
    //     replace: true,
    //     query: {
    //       noGoBack: true,
    //     },
    //   });
    // }

  }
});

router.afterEach(() => {
  // finish progress bar
  NProgress.done();
});

export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;