const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // 开发服务器配置
  devServer: {
    proxy: {
      // 将所有 /passkey 开头的请求代理到后端服务器
      '/passkey': {
        target: 'http://localhost:8888', // 替换为你的后端服务器地址
        changeOrigin: true,
        secure: false,
        // 如果后端 API 不是以 /passkey 开头，可以使用 pathRewrite
        // pathRewrite: {
        //   '^/passkey': '/api/passkey' // 例如将 /passkey 重写为 /api/passkey
        // }
      }
    }
  }
})
