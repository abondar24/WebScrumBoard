import Vue from 'vue'
import Router from 'vue-router'
import Hello from '../components/Hello'
import Login from "../components/Login";
import Register from "../components/Register";
import User from "../components/User";
import PasswordReset from "../components/PasswordReset";

Vue.use(Router);

const router = new Router({
  routes: [
    {path: '/', name: 'Hello', component: Hello},
    {path: '/login',name:'Login', component: Login},
    {path: '/register',name:'Register', component: Register},
    {path: '/user/:id' ,name:'User', component: User},
    {path: '/reset',name:'PasswordReset', component: PasswordReset},
    { path: '*', redirect: '/' }
  ]
});



// router.beforeEach((to, from, next) => {
//   if (this.$store.state.auth.authenticated) {
//     next();
//   } else {
//     next('/');
//   }
// });

export default router;
