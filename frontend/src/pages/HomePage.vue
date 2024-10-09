<script setup lang="ts">
import {useRoute} from "vue-router";

const route = useRoute()
const {name, params, query} = route
console.log('name', name)
console.log('params', params)
console.log('query', query)
</script>

<template>
    <router-link to="/">메인</router-link>
    <router-link to="/sign-up">회원가입</router-link>
    <router-link to="/sign-in">로그인</router-link>
</template>

<style scoped>

</style>
