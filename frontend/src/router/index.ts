import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import SignInPage from '../pages/SignInPage.vue';
import SignUpPage from '../pages/SignUpPage.vue';
import MainPage from '../pages/MainPage.vue';
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        component: MainPage
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
