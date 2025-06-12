# 🔑 Passkey认证系统 (Spring Boot + Vue)

> **一个简易的无密码认证的现代化实现** - 基于FIDO2标准，使用生物识别/设备PIN替代传统密码，提升安全性与用户体验

<div style="display: flex; justify-content: space-between;">
<img src="https://github.com/user-attachments/assets/980b054c-2051-4aa7-85c7-1947eabb642a" alt="singin" width="450">
<img src="https://github.com/user-attachments/assets/b569ff83-0f20-44a1-83e2-476052554eb3" alt="login" width="450">
</div>

## 目录

- 🧩 项目结构
- ✨ 核心特性
- 🛠️ 技术栈
- 🚀 快速启动
  - 后端配置
  - 前端配置
- 🔧 使用指南
  - 用户注册流程
  - 登录认证流程
- 🌐 API参考

------

## 🧩 项目结构

```
passkey-demo/
├── backend/                 # Spring Boot后端
│   ├── src/
│   │   ├── main/java/.../passkeydemo/
│   │   │   ├── config/      # 安全配置
│   │   │   ├── controller/  # API端点
│   │   │   ├── event/       # 事件
│   │   │   ├── listener/    # 事件监听
│   │   │   ├── mapper/      # 映射
│   │   │   ├── model/       # 数据模型
│   │   │   └── service/     # 认证逻辑
│   │   └── resources/       # 配置文件
│
└── frontend/                # Vue前端
    ├── src/
    │   ├── components/      # 组件
    │   ├── route/           # 路由
    │   ├── service/         # API封装
    │   └── views/           # 页面
```

## ✨ 核心特性

- **无密码认证**：利用设备生物识别（指纹/面部）或PIN码替代传统密码
- **FIDO2标准实现**：符合WebAuthn规范的安全挑战-响应协议
- **双端完整流程**：
  - 注册阶段：客户端生成非对称密钥对，服务端安全存储公钥
  - 登录阶段：私钥签名验证挑战，服务端用公钥验签
- **抗钓鱼攻击**：密钥绑定域名，防止重定向攻击

------

## 🛠️ 技术栈

|     层     |                         技术组件                         |
| :--------: | :------------------------------------------------------: |
|  **后端**  | Spring Boot 3.5.0, MyBatis-Plus 3.5.7, Java 17, webauthn |
|  **前端**  |                        Vue 3.2.13                        |
| **数据库** |                        MySQL 8.0                         |

------

## 🚀 快速启动

### 后端配置

```bash
# 克隆项目
git clone https://github.com/lklbjn/passkey_demo.git
cd passkey_demo/backend

# 配置数据库
src/main/resources/application.yml
```

### 前端配置

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

> **注意**：必须使用HTTPS！本地测试可用localhost

------

## 🔧 使用指南

### 用户注册流程

1. 访问 `http://localhost:8193`
2. 点击注册 → 输入用户名 → 点击「注册Passkey」
3. 按系统提示完成生物识别/PIN验证
4. 后端自动存储公钥，完成注册

### 登录认证流程

1. 访问 `https://localhost:8193`
2. 点击登录 → 点击「使用Passkey登录」
3. 使用设备生物识别解锁私钥
4. 系统验证签名后返回登录状态

> **流程安全机制**：每次认证生成唯一挑战码（challenge），5分钟过期
