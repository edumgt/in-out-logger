import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import SignInPage from '../pages/SignInPage.vue';
import SignUpPage from '../pages/SignUpPage.vue';
import MainPage from '../pages/MainPage.vue';
import CalendarPage from '../pages/CalendarPage.vue'
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
    {
        path: '/calendar',
        name: 'calendar',
        component: CalendarPage
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
