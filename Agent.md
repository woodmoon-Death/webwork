# Agent.md

## 项目执行定位

本项目已明确跳过前三个学习阶段，直接进入最终阶段开发。后续所有实现都应按前后端分离的完整社交分享平台推进，不再制作单纯静态网页原型，也不再以纯前端模拟数据作为最终目标。

最终技术路线：

- 前端：Vue3、Vite、Vue Router、Pinia 或 Composition API Store、Axios。
- 后端：Java、Spring Boot、Spring MVC、MyBatis 或 MyBatis-Plus、MySQL。
- 文件上传：前端 `multipart/form-data`，后端 `MultipartFile`。
- 权限控制：前端控制显示，后端负责最终校验。

## 产品目标

构建一个完整的社交分享平台，支持：

- 用户注册、登录、退出。
- 首页信息流浏览。
- 文字分享发布。
- 图片上传和展示。
- 分享详情查看。
- 评论发布和删除。
- 点赞和取消点赞。
- 个人主页。
- 管理员内容管理。
- 基于角色的权限控制。

角色包括：

- `GUEST`：游客，只能浏览公开内容。
- `USER`：普通用户，可以发布、点赞、评论、管理自己的内容。
- `ADMIN`：管理员，可以管理全部内容。

## 开发原则

- 以后端数据库为真实数据源，不把 `localStorage` 当最终数据源。
- 前端权限只用于改善体验，安全必须由后端兜底。
- API 请求、错误处理、权限判断都要集中封装。
- 页面优先做真实可用的信息流产品，不做营销落地页。
- 功能开发按闭环推进：接口、页面、状态、错误处理一起完成。
- 避免把大量逻辑堆在单个 Vue 页面或单个 Java Controller 中。

## 前端结构

推荐结构：

```text
frontend/
  package.json
  vite.config.js
  src/
    main.js
    App.vue
    router/
      index.js
    api/
      http.js
      authApi.js
      userApi.js
      postApi.js
      commentApi.js
      fileApi.js
      adminApi.js
    stores/
      userStore.js
      postStore.js
    views/
      LoginView.vue
      RegisterView.vue
      HomeView.vue
      PublishView.vue
      PostDetailView.vue
      ProfileView.vue
      AdminView.vue
    components/
      AppHeader.vue
      PostCard.vue
      PostForm.vue
      ImageUploader.vue
      CommentList.vue
      CommentForm.vue
      LikeButton.vue
      UserBadge.vue
      EmptyState.vue
      LoadingState.vue
      PermissionNotice.vue
    assets/
      styles/
        base.css
        layout.css
        components.css
```

职责说明：

- `api/http.js`：Axios 实例、请求拦截器、响应拦截器、错误处理。
- `api/*Api.js`：按业务模块封装接口请求。
- `stores/userStore.js`：登录状态、当前用户、角色权限。
- `stores/postStore.js`：信息流、详情、筛选条件、分页状态。
- `views/`：页面级组件。
- `components/`：可复用 UI 和业务组件。

## 后端结构

推荐结构：

```text
backend/
  pom.xml
  src/main/java/
    com/example/social/
      SocialApplication.java
      config/
        WebConfig.java
        CorsConfig.java
      controller/
        AuthController.java
        UserController.java
        PostController.java
        CommentController.java
        FileController.java
        AdminController.java
      service/
        AuthService.java
        UserService.java
        PostService.java
        CommentService.java
        LikeService.java
        FileService.java
        AdminService.java
      mapper/
        UserMapper.java
        PostMapper.java
        CommentMapper.java
        LikeMapper.java
        FileMapper.java
      entity/
      dto/
      vo/
      exception/
      security/
  src/main/resources/
    application.yml
    mapper/
  uploads/
```

分层规则：

- Controller 只处理请求参数、调用 Service、返回结果。
- Service 负责业务规则、权限判断、事务控制。
- Mapper 只负责数据库访问。
- DTO 接收请求数据。
- VO 返回给前端，不能包含密码哈希等敏感字段。
- Exception 统一处理业务异常和系统异常。

## 数据库表

必须包含：

- `users`：用户账号、昵称、密码哈希、角色、头像、状态。
- `posts`：分享标题、正文、图片 URL、作者、可见状态。
- `comments`：评论内容、所属分享、作者。
- `likes`：用户与分享的点赞关系，必须用唯一约束防止重复点赞。
- `files`：上传文件原名、存储路径、URL、类型、大小。

## API 约定

统一响应格式：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

认证接口：

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

分享接口：

```text
GET    /api/posts
POST   /api/posts
GET    /api/posts/{id}
PUT    /api/posts/{id}
DELETE /api/posts/{id}
PATCH  /api/posts/{id}/visibility
```

评论接口：

```text
GET    /api/posts/{id}/comments
POST   /api/posts/{id}/comments
DELETE /api/comments/{id}
```

点赞接口：

```text
POST   /api/posts/{id}/like
DELETE /api/posts/{id}/like
```

文件接口：

```text
POST /api/files/upload
```

管理接口：

```text
GET /api/admin/stats
GET /api/admin/posts
```

## 权限规则

后端必须实现权限校验：

| 操作 | GUEST | USER | ADMIN |
|---|---:|---:|---:|
| 浏览公开内容 | 可以 | 可以 | 可以 |
| 注册 / 登录 | 可以 | 可以 | 可以 |
| 发布分享 | 不可以 | 可以 | 可以 |
| 上传图片 | 不可以 | 可以 | 可以 |
| 点赞 / 取消点赞 | 不可以 | 可以 | 可以 |
| 发布评论 | 不可以 | 可以 | 可以 |
| 删除自己的分享 | 不可以 | 可以 | 可以 |
| 删除任意分享 | 不可以 | 不可以 | 可以 |
| 删除自己的评论 | 不可以 | 可以 | 可以 |
| 删除任意评论 | 不可以 | 不可以 | 可以 |
| 隐藏 / 恢复内容 | 不可以 | 不可以 | 可以 |
| 查看管理页 | 不可以 | 不可以 | 可以 |

前端可以根据角色隐藏按钮，但所有敏感操作必须等待后端确认。

## 前端页面要求

必须实现：

- 登录页：账号密码登录、错误提示。
- 注册页：用户名、昵称、密码、确认密码校验。
- 首页：信息流、搜索、筛选、点赞入口、评论入口。
- 发布页：标题、正文、图片上传、图片预览、发布校验。
- 详情页：分享正文、图片、点赞、评论列表、评论发布。
- 个人主页：用户信息、个人发布、统计数据。
- 管理页：内容管理、隐藏恢复、删除、平台统计。

## UI/UX 要求

- 首页第一屏必须是可浏览的信息流体验。
- 不做空泛宣传页。
- 卡片圆角不超过 8px。
- 按钮、输入框、筛选项尺寸稳定，避免 hover 后布局跳动。
- 所有可点击元素要有 `cursor: pointer` 和清晰反馈。
- 所有表单输入有 label 或可访问名称。
- 所有图片有 alt。
- 使用 `textContent` 或 Vue 默认文本插值展示用户输入，避免 XSS。
- 移动端 375px 宽度下不能出现横向滚动。

## 开发顺序

按以下顺序推进，不要跳着做孤立页面：

1. 后端工程、数据库连接、统一响应、异常处理。
2. 数据库表、实体、Mapper。
3. 注册、登录、当前用户接口。
4. 前端工程、路由、Axios 封装、用户 Store。
5. 登录页、注册页、登录状态保持。
6. 分享列表 API 和首页信息流。
7. 发布分享 API 和发布页。
8. 图片上传 API 和图片预览。
9. 分享详情页。
10. 评论列表、发布、删除。
11. 点赞、取消点赞。
12. 个人主页。
13. 管理员管理页。
14. 响应式、错误状态、加载状态、最终验收。

## 安全要求

必须遵守：

- 密码不能明文存储。
- 登录凭证不能暴露敏感信息。
- 后端不能信任前端传来的角色。
- 普通用户不能通过改请求删除他人内容。
- 上传文件必须校验类型、大小和保存路径。
- 数据库查询避免 SQL 注入。
- 返回给前端的数据不能包含 `password_hash`。
- 管理接口必须限制 `ADMIN` 角色。

## 验收清单

完成前检查：

- 用户可以注册、登录、退出。
- 游客只能浏览公开内容。
- 登录用户可以发布文字和图片。
- 登录用户可以评论、点赞、取消点赞。
- 用户可以删除自己的分享和评论。
- 管理员可以删除、隐藏、恢复任意内容。
- 图片上传后刷新仍可显示。
- 数据保存到 MySQL。
- 前端刷新后能恢复登录状态或正确回到登录状态。
- 所有 API 错误都有用户可理解的提示。
- 移动端布局可用。
- 后端权限校验真实生效。

## 禁止事项

- 不要把最终项目做成纯静态 HTML 页面。
- 不要只用 `localStorage` 假装完成后端。
- 不要只在前端判断权限。
- 不要明文保存密码。
- 不要把所有 Vue 逻辑写在一个组件里。
- 不要把所有 Java 逻辑写在一个 Controller 里。
