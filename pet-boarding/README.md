# 宠物寄养管理系统

基于组件化技术的宠物寄养管理系统，采用前后端分离架构。

## 技术栈

- **前端**: Vue 3 + Element Plus + Vite + Pinia + Vue Router
- **后端**: Spring Boot 2.7 + MyBatis-Plus 3.5 + MySQL 8.0
- **认证**: JWT

## 项目结构

```
pet-boarding/
├── sql/                        # 数据库脚本
│   └── init.sql               # 初始化脚本（14张表 + 测试数据）
├── pet-boarding-backend/       # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/petboarding/
│       │   ├── config/        # 配置类
│       │   ├── common/        # 通用类（Result、Constants）
│       │   ├── utils/         # 工具类（JwtUtils）
│       │   ├── interceptor/   # 拦截器（JWT认证）
│       │   ├── exception/     # 异常处理
│       │   ├── entity/        # 实体类（14个）
│       │   ├── mapper/        # MyBatis Mapper（14个）
│       │   ├── service/       # 服务层（14个接口 + 实现）
│       │   └── controller/    # 控制器（13个）
│       └── resources/
│           └── application.yml
└── pet-boarding-frontend/      # Vue 3 前端
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/               # API接口模块（13个）
        ├── router/            # 路由配置
        ├── store/             # Pinia状态管理
        ├── utils/             # Axios封装
        ├── components/        # 布局组件
        └── views/             # 页面视图
            ├── owner/         # 宠物主人端（7个页面）
            └── provider/      # 服务商端（10个页面）
```

## 快速启动

### 1. 数据库初始化

```bash
# 确保 MySQL 已启动，然后执行初始化脚本
mysql -u root -p < sql/init.sql
```

### 2. 启动后端

```bash
cd pet-boarding-backend

# 修改数据库连接配置（如需要）
# 编辑 src/main/resources/application.yml

# 编译并启动
mvn clean spring-boot:run
```

后端启动后访问: http://localhost:8080

### 3. 启动前端

```bash
cd pet-boarding-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问: http://localhost:5173

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 宠物主人 | owner_zhang | 123456 |
| 宠物主人 | owner_li | 123456 |
| 宠物主人 | owner_wang | 123456 |
| 服务商 | provider_sun | 123456 |
| 服务商 | provider_zhao | 123456 |

## 功能模块

### 宠物主人端
- 注册/登录/实名认证
- 宠物信息管理（CRUD + 健康档案 + 紧急联系人）
- 服务搜索与筛选
- 订单管理（创建/支付/跟踪/取消）
- 寄养过程互动（查看照料记录）
- 服务评价与投诉反馈
- 优惠券领取使用
- 积分累计兑换
- 消息通知中心

### 服务商端
- 注册/登录/资质认证
- 店铺信息管理
- 服务项目上架/下架/定价
- 订单接单/拒单/入托/出托
- 照料记录上传
- 评价回复
- 投诉申诉处理
- 员工管理（信息/考勤）
- 营销活动管理（优惠券）
