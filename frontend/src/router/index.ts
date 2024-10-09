import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import HomePage from '../pages/HomePage.vue'
import SignInPage from '../pages/SignInPage.vue';
import SignUpPage from '../pages/SignUpPage.vue';
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        component: HomePage
    },
    {
        path: '/sign-in',
        name: 'signIn',
        component: SignInPage
    },
    {
        path: '/sign-up',
        name: 'signUp',
        component: SignUpPage
    },
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
