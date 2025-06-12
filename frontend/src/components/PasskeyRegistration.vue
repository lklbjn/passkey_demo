<template>
  <div class="passkey-registration">
    <h2>Passkey 注册</h2>
    
    <div v-if="!isRegistered">
      <div class="form-group">
        <label for="userId">用户 ID:</label>
        <input 
          type="number" 
          id="userId" 
          v-model="userId" 
          class="form-control"
          placeholder="输入用户 ID"
        />

        <label for="describe">Passkey 描述:</label>
        <input 
          type="text" 
          id="describe" 
          v-model="describe" 
          class="form-control"
          placeholder="输入Passkey 描述"
        />
      </div>
      
      <button 
        @click="registerPasskey" 
        class="btn btn-primary" 
        :disabled="isRegistering || !userId"
      >
        {{ isRegistering ? '注册中...' : '注册 Passkey' }}
      </button>
    </div>
    
    <div v-else class="success-message">
      <p>Passkey 注册成功!</p>
      <button @click="reset" class="btn btn-secondary">重新注册</button>
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
  name: 'PasskeyRegistration',
  setup() {
    const userId = ref('');
    const describe = ref('');
    const isRegistering = ref(false);
    const isRegistered = ref(false);
    const errorMessage = ref('');
    
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
      console.info('base64UrlToArrayBuffer:', base64Url);
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
    
    const registerPasskey = async () => {
      if (!userId.value) {
        errorMessage.value = '请输入用户 ID';
        return;
      }
      
      isRegistering.value = true;
      errorMessage.value = '';
      
      try {
        // 1. 获取注册选项
        let options = await passkeyService.getRegistrationOptions(userId.value);
        // 如果返回的是字符串，则解析它
        if (typeof options === 'string') {
            options = JSON.parse(options);
        }
        options = options.publicKey
        console.info('publicKey:', options);

        options.user.id = base64UrlToArrayBuffer(options.user.id);
        options.challenge = base64UrlToArrayBuffer(options.challenge);
        
        // 2. 创建凭证
        const cred = await navigator.credentials.create({
          publicKey: options
        });
        console.info('cred:', cred);

        const credential = {
            id: cred.id,
            rawId: arrayBufferToBase64Url(cred.rawId),
            type: cred.type,
            authenticatorAttachment: cred.authenticatorAttachment,
            clientExtensionResults: cred.getClientExtensionResults ? cred.getClientExtensionResults() : [],
            response: {
                clientDataJSON: arrayBufferToBase64Url(cred.response.clientDataJSON),
                attestationObject: arrayBufferToBase64Url(cred.response.attestationObject),
                transports: cred.response.getTransports ? cred.response.getTransports() : []
            }
        };
        
        // 3. 验证注册
        const credentialJson = JSON.stringify(publicKeyCredentialToJSON(credential));
        console.info('credentialJson:', credentialJson);
        const requestData = {
          userId: userId.value,
          credential: credentialJson,
          describe: describe.value
        };
        await passkeyService.verifyRegistration(requestData);
        
        isRegistered.value = true;
      } catch (error) {
        console.error('Passkey registration error:', error);
        errorMessage.value = `注册失败: ${error.message || '未知错误'}`;
      } finally {
        isRegistering.value = false;
      }
    };
    
    const reset = () => {
      userId.value = '';
      describe.value = '';
      isRegistered.value = false;
      errorMessage.value = '';
    };
    
    return {
      userId,
      describe,
      isRegistering,
      isRegistered,
      errorMessage,
      registerPasskey,
      reset
    };
  }
};
</script>

<style scoped>
.passkey-registration {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 15px;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #2196F3;
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
</style>
