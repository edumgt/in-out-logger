import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import UserPage from '../pages/UserPage.vue';
import HomePage from '../pages/HomePage.vue';
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        component: HomePage
    },
    {
        path: '/user',
        name: 'user',
        component: UserPage
    },
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
