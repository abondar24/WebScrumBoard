import Vue from 'vue'
import Router from 'vue-router'
import Hello from '../components/Hello'
import Login from "../components/Login";
import Register from "../components/Register";
import User from "../components/User";
import PasswordReset from "../components/PasswordReset";
import store from "../store/store"
import UserProjects from "../components/UserProjects";
import Project from "../components/Project";
import ProjectBoard from "../components/ProjectBoard";

Vue.use(Router);

export default new Router({
    routes: [
        {path: '/', name: 'Hello', component: Hello},
        {path: '/login', name: 'Login', component: Login},
        {path: '/register', name: 'Register', component: Register},
        {path: '/user/:id', name: 'User', component: User,
            beforeEnter(to, from, next) {
                if (store.state.auth.authenticated) {
                    next();
                } else {
                    next('/');
                }
            }
        },
        {path: '/project/:id', name: 'Project', component: Project,
            beforeEnter(to, from, next) {
                if (store.state.auth.authenticated) {
                    next();
                } else {
                    next('/');
                }
            }
        },
        {path: '/project/:id/board', name: 'ProjectBoard', props: true, component: ProjectBoard,
            beforeEnter(to, from, next) {
                if (store.state.auth.authenticated) {
                    next();
                } else {
                    next('/');
                }
            }
        },
        {path: '/reset', name: 'PasswordReset', component: PasswordReset},
        {
            path: '/projects', name: 'UserProjects', component: UserProjects,
            beforeEnter(to, from, next) {
                if (store.state.auth.authenticated) {
                    next();
                } else {
                    next('/');
                }
            }
        },
        {path: '*', redirect: '/'}
    ]
});

