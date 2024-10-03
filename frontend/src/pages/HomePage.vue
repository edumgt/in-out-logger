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
    <router-link to="/user">회원가입/로그인</router-link>
</template>

<style scoped>

</style>
