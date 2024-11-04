import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '../views/Login.vue'
import Secret from '../views/Secret.vue'
import { auth } from '../firebase'
import {getAuth, onAuthStateChanged} from "firebase/auth";

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/secret',
    name: 'secret',
    component: Secret,
    meta: {needsAuth: true}
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {

//   //NO ACCESS TO SECRET VIEW
//   if(to.meta.needsAuth){
//     alert("You don't have access!")
//     next("/");
//   }else{
//      if (to.matched.some(record => record.meta.requiresAuth) && !auth.currentUser) {
//      next('/secret')
//      return;
//    }
//    next();
//   }
// });

//ACCESS TO ALL
  if (to.path === '/login' && auth.currentUser) {
    next('/')
    return;
  }

  if (to.matched.some(record => record.meta.requiresAuth) && !auth.currentUser) {
    next('/login')
    return;
  }

  next();
});

export default router
