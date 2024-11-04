<template>
  <div>
    <div v-if="$store.state.user">
      <nav>
        <router-link to="/">Home</router-link> |
        <router-link to="/about">About</router-link> |
        <router-link to="/secret">Secret</router-link> |
        <button @click="$store.dispatch('logout')">Logout</button>
      </nav>
    </div>   

    <!-- current element transitions out first and then the new element transitions in -->
    <router-view v-slot="{ Component }">
      <transition name="slide-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script>
import { onBeforeMount } from 'vue'
import { useStore } from 'vuex'

export default {
  setup() {
    const store = useStore()

    // Dispatches an action named 'fetchUser' to 
    // retrieve user data from the store before the component is mounted
    onBeforeMount(() => {
      store.dispatch('fetchUser')
    })
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

/* FADE TRANSITION */

/* What style are we starting from and leaving to? Invisible */
/* We are transitioning to opacity 1 which is the default so we do not need to add this */

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* What is the active entering and leaving style? */
.fade-enter-active {
  transition: opacity 1s ease-in;
}

.fade-leave-active {
  transition: opacity 1s ease-in;
}

/* SLIDE-FADE TRANSITION */

.slide-fade-leave-to {
  transform: translateX(50px);
  opacity: 0;
}

.slide-fade-enter-from {
  transform: translateX(-50px);
  opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  /* transition: opacity 1s ease-out; */
  transition: all 2s ease;
}

/* SLIDE-UP TRANSITION */

.slide-up-enter-from {
  transform: translateY(10px);
  opacity: 0;
}

.slide-up-enter-active {
  /* transition: opacity 1s ease-out; */
  transition: all 1s ease;
}
</style>
