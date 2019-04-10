<template>
  <div class="logo">
    <router-link :to="{name:'dashboard'}">
      <img src="~@/assets/logo.png" alt="logo">
      <h1 v-if="showTitle">{{ title }}</h1>
    </router-link>
  </div>
</template>

<script>
  export default {
    name: "Logo",
    props: {
      title: {
        type: String,
        default: 'JX-Boot',
        required: false
      },
      showTitle: {
        type: Boolean,
        default: true,
        required: false
      }
    }
  }
</script>
<style lang="scss" scoped>
  /*缩小首页布 局顶部的高度*/
  .sider .logo {
    height: 59px!important;
    line-height: 59px!important;
  }
</style>