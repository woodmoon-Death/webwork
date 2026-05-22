# Railway 部署准备说明

本项目是前后端分离仓库，建议在 Railway 中创建 3 个服务：

- `shareflow-mysql`：Railway MySQL 数据库
- `shareflow-backend`：Spring Boot 后端，Root Directory 选择 `backend`
- `shareflow-frontend`：Vue/Vite 前端，Root Directory 选择 `frontend`

## 1. 创建 MySQL 服务

在 Railway 项目里添加 MySQL 数据库服务。创建后进入数据库服务，使用 Query/Console 执行：

```sql
source backend/src/main/resources/schema.sql;
```

如果 Railway 控制台不支持 `source`，就打开仓库中的 `backend/src/main/resources/schema.sql`，复制全部 SQL 后粘贴执行。

## 2. 部署后端服务

从 GitHub 仓库部署，服务 Root Directory 设置为：

```text
backend
```

如果 Railway 没有自动识别配置文件，把 Config File Path 设置为：

```text
/backend/railway.json
```

`backend/railway.json` 已配置：

- 构建命令：`mvn -DskipTests package`
- 启动命令：`java -jar target/social-share-0.0.1-SNAPSHOT.jar`

后端服务环境变量：

```text
APP_CORS_ALLOWED_ORIGINS=https://你的前端域名.up.railway.app
APP_UPLOAD_DIR=uploads
```

数据库变量如果后端和 MySQL 在同一个 Railway 项目中，一般可以引用 MySQL 服务变量：

```text
MYSQLHOST=${{shareflow-mysql.MYSQLHOST}}
MYSQLPORT=${{shareflow-mysql.MYSQLPORT}}
MYSQLDATABASE=${{shareflow-mysql.MYSQLDATABASE}}
MYSQLUSER=${{shareflow-mysql.MYSQLUSER}}
MYSQLPASSWORD=${{shareflow-mysql.MYSQLPASSWORD}}
```

也可以直接设置 Spring Boot 标准变量：

```text
SPRING_DATASOURCE_URL=jdbc:mysql://主机:端口/数据库名?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=用户名
SPRING_DATASOURCE_PASSWORD=密码
```

## 3. 部署前端服务

从同一个 GitHub 仓库再创建一个服务，Root Directory 设置为：

```text
frontend
```

如果 Railway 没有自动识别配置文件，把 Config File Path 设置为：

```text
/frontend/railway.json
```

`frontend/railway.json` 已配置：

- 构建命令：`npm ci && npm run build`
- 启动命令：`npm run start`

前端服务环境变量：

```text
VITE_API_BASE_URL=https://你的后端域名.up.railway.app/api
VITE_ASSET_BASE_URL=https://你的后端域名.up.railway.app
```

修改环境变量后重新部署前端。

## 4. 部署后检查

1. 打开前端公网域名，确认页面能加载。
2. 注册一个新账号。
3. 发布一条文字分享。
4. 上传图片并确认图片能显示。
5. 点赞、评论、个人主页和通知功能都能访问。

## 注意

Railway 的文件系统通常不适合作为长期图片存储。当前项目会把上传图片保存到后端服务本地 `uploads` 目录，重新部署后图片可能丢失。正式使用建议接入对象存储，例如 S3、Cloudflare R2、Supabase Storage。
