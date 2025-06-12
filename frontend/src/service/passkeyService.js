import axios from 'axios';

export default {
  // 获取 Passkey 注册选项
  async getRegistrationOptions(userId) {
    try {
      const response = await axios.get(`/passkey/registration/options/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting registration options:', error);
      throw error;
    }
  },

  // 验证 Passkey 注册
  async verifyRegistration(requestData) {
    try {
      const response = await axios.post(`/passkey/registration`, requestData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error verifying registration:', error);
      throw error;
    }
  },

  // 获取 Passkey 断言选项（登录）
  async getAssertionOptions() {
    try {
      const response = await axios.get(`/passkey/assertion/options`, { withCredentials: true });
      return response.data;
    } catch (error) {
      console.error('Error getting assertion options:', error);
      throw error;
    }
  },

  // 验证 Passkey 断言（登录）
  async verifyAssertion(credential) {
    try {
      const response = await axios.post(`/passkey/assertion`, credential, {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      console.error('Error verifying assertion:', error);
      throw error;
    }
  }
};
