<template>
  <div class="home">
    <h1>Passkey 认证演示</h1>
    
    
    <div class="tabs">
      <button 
        @click="activeTab = 'register'" 
        :class="{ active: activeTab === 'register' }"
        class="tab-btn"
      >
        注册
      </button>
      <button 
        @click="activeTab = 'login'" 
        :class="{ active: activeTab === 'login' }"
        class="tab-btn"
      >
        登录
      </button>
    </div>
    
    <div class="tab-content">
      <PasskeyRegistration v-if="activeTab === 'register'" />
      <PasskeyLogin v-if="activeTab === 'login'" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import PasskeyRegistration from '../components/PasskeyRegistration.vue';
import PasskeyLogin from '../components/PasskeyLogin.vue';

export default {
  name: 'HomePage',
  components: {
    PasskeyRegistration,
    PasskeyLogin
  },
  setup() {
    const activeTab = ref('register');
    
    return {
      activeTab
    };
  }
};
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  margin: 0 10px;
  border: none;
  background-color: #f1f1f1;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
}

.tab-btn.active {
  background-color: #4CAF50;
  color: white;
}

.tab-content {
  margin-top: 20px;
}
</style>
