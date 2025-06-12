<template>
  <div class="passkey-login">
    <h2>Passkey 登录</h2>
    
    <div v-if="!isLoggedIn">
      <button 
        @click="loginWithPasskey" 
        class="btn btn-primary" 
        :disabled="isLoggingIn"
      >
        {{ isLoggingIn ? '登录中...' : '使用 Passkey 登录' }}
      </button>
    </div>
    
    <div v-else class="success-message">
      <p>登录成功!</p>
      <div class="user-info">
        <p><strong>用户 ID:</strong> {{ user?.userId }}</p>
        <p><strong>用户名:</strong> {{ user?.username }}</p>
      </div>
      <button @click="logout" class="btn btn-secondary">登出</button>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import passkeyService from '@/service/passkeyService';

export default {
  name: 'PasskeyLogin',
  setup() {
    const isLoggingIn = ref(false);
    const isLoggedIn = ref(false);
    const user = ref(null);
    const errorMessage = ref('');
    
    // 预处理获取选项
    const preformatGetOptions = (options) => {
      // 确保 options 是有效的对象
      if (!options) {
        console.error('Assertion options is undefined or null');
        return null;
      }

      // 创建一个新对象，避免修改原始对象
      const formattedOptions = { ...options };
      
      // 将 base64 字符串转换为 ArrayBuffer
      if (formattedOptions.challenge) {
        formattedOptions.challenge = base64UrlToArrayBuffer(formattedOptions.challenge);
      }
      
      // 安全地处理 allowCredentials
      if (formattedOptions.allowCredentials && Array.isArray(formattedOptions.allowCredentials)) {
        formattedOptions.allowCredentials = formattedOptions.allowCredentials.map(credential => {
          if (credential?.id) {
            return {
              ...credential,
              id: base64UrlToArrayBuffer(credential.id)
            };
          }
          return credential;
        });
      } else {
        // 如果 allowCredentials 不存在或不是数组，设置为空数组
        formattedOptions.allowCredentials = [];
      }
      
      return formattedOptions;
    };
    
    // 将 PublicKeyCredential 转换为 JSON
    const publicKeyCredentialToJSON = (credential) => {
      if (credential instanceof Array) {
        return credential.map(publicKeyCredentialToJSON);
      }
      
      if (credential instanceof ArrayBuffer) {
        return arrayBufferToBase64Url(credential);
      }
      
      if (credential instanceof Object) {
        const obj = {};
        
        for (const key in credential) {
          obj[key] = publicKeyCredentialToJSON(credential[key]);
        }
        
        return obj;
      }
      
      return credential;
    };
    
    // Base64URL 转 ArrayBuffer
    const base64UrlToArrayBuffer = (base64Url) => {
      const padding = '='.repeat((4 - (base64Url.length % 4)) % 4);
      const base64 = (base64Url + padding)
        .replace(/-/g, '+')
        .replace(/_/g, '/');
      
      const rawData = window.atob(base64);
      const buffer = new Uint8Array(rawData.length);
      
      for (let i = 0; i < rawData.length; i++) {
        buffer[i] = rawData.charCodeAt(i);
      }
      
      return buffer.buffer;
    };
    
    // ArrayBuffer 转 Base64URL
    const arrayBufferToBase64Url = (arrayBuffer) => {
      const bytes = new Uint8Array(arrayBuffer);
      let str = '';
      
      for (const byte of bytes) {
        str += String.fromCharCode(byte);
      }
      
      const base64 = window.btoa(str);
      
      return base64
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=/g, '');
    };
    
    const loginWithPasskey = async () => {
      isLoggingIn.value = true;
      errorMessage.value = '';
      
      try {
        // 1. 获取断言选项
        let options = await passkeyService.getAssertionOptions();
        // 如果返回的是字符串，则解析它
        if (typeof options === 'string') {
          options = JSON.parse(options);
        }
        options = options.publicKey
        console.info('options:', options);

                
        // 2. 获取凭证
        const cred = await navigator.credentials.get({
          publicKey: preformatGetOptions(options)
        });
        console.info('cred:', cred);
        
        const credential = {
            id: cred.id,
            rawId: arrayBufferToBase64Url(cred.rawId),
            type: cred.type,
            authenticatorAttachment: cred.authenticatorAttachment,
            clientExtensionResults: cred.getClientExtensionResults ? cred.getClientExtensionResults() : [],
            response: {
                authenticatorData: arrayBufferToBase64Url(cred.response.authenticatorData),
                clientDataJSON: arrayBufferToBase64Url(cred.response.clientDataJSON),
                signature: arrayBufferToBase64Url(cred.response.signature),
                userHandle: arrayBufferToBase64Url(cred.response.userHandle),
            }
        };
        
        // 3. 验证断言
        const credentialJson = JSON.stringify(credential);
        console.info('credentialJson:', credentialJson);
        const response = await passkeyService.verifyAssertion(credentialJson);
        
        isLoggedIn.value = true;
        user.value = response;
      } catch (error) {
        console.error('Passkey login error:', error);
        errorMessage.value = `登录失败: ${error.message || '未知错误'}`;
      } finally {
        isLoggingIn.value = false;
      }
    };
    
    const logout = () => {
      isLoggedIn.value = false;
      user.value = null;
    };
    
    return {
      isLoggingIn,
      isLoggedIn,
      user,
      errorMessage,
      loginWithPasskey,
      logout
    };
  }
};
</script>

<style scoped>
.passkey-login {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.btn {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.btn-primary {
  background-color: #2196F3;
  color: white;
}

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #f44336;
  color: white;
  margin-top: 10px;
}

.error-message {
  color: #f44336;
  margin-top: 15px;
}

.success-message {
  color: #4CAF50;
  margin-top: 15px;
}

.user-info {
  background-color: #e8f5e9;
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
}
</style>

