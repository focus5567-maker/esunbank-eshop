<template>
  <div class="app">
    <nav class="nav">
      <router-link to="/products">商品管理</router-link>
      <router-link to="/orders">建立訂單</router-link>
    </nav>
    <router-view />
  </div>
</template>

<style>
.app {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, "Microsoft JhengHei", sans-serif;
}
.nav {
  margin-bottom: 20px;
}
.nav a {
  margin-right: 20px;
  text-decoration: none;
  color: #2c3e50;
  font-weight: bold;
}
.nav a.router-link-active {
  color: #42b983;
}
</style>