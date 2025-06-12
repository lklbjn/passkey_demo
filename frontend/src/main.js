import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index' // 这里会自动找到 ./router/index.js

// 检查浏览器是否支持 WebAuthn
const isWebAuthnSupported = () => {
  return window.PublicKeyCredential !== undefined &&
         typeof window.PublicKeyCredential === 'function';
};

// 如果浏览器不支持 WebAuthn，显示警告
if (!isWebAuthnSupported()) {
  alert('您的浏览器不支持 WebAuthn/Passkey。请使用最新版本的 Chrome、Firefox、Safari 或 Edge 浏览器。');
}

createApp(App).use(router).mount('#app')
