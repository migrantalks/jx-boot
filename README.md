### JX-Boot 快速开发平台

本项目采用前端分离方式，进行快速开发，减少开发成本，提升开发效率

借鉴项目 https://github.com/zhangdaiscott/jeecg-boot


#### 技术架构

##### 后端

- 基础框架：Spring Boot

- 持久层框架：Mybatis-plus

- 安全框架：Apache Shiro，Jwt

- 数据库连接池：Druid

- 缓存框架：Redis

- 日志打印：logback

- 其他：Swagger-ui，quartz，lombok 等。

##### 前端

- Vue，Vuex，Vue Router

- Axios

- ant-design-vue

- webpack，npm 等


#### 开发环境

- 语言：Java 8

- IDE(JAVA)：Eclipse 或 IDEA

- IDE(前端)：Sublime Text 或 Webstorm

- 依赖管理：Maven

- 数据库：MySQL 5.0 或 Oracle 11g

- 缓存：Redis


#### 项目下载和运行

- 通过 git 获取代码

```bash
git clone https://github.com/migrantalks/jx-boot.git
```

- 项目说明

> jx-boot 是后端代码
>
> jx-vue 是前端代码


- 后端代码运行

> 通过 IDEA 导入项目，运行 pom.xml ，下载相关依赖包
>
> 创建数据库 jx-boot ，执行 jx-boot/docs/db/init.sql，生成相关基础表
>
> 启动 Redis 服务
> 
> IDEA 运行项目 
>
> 访问项目 http://localhost:8080/jx-boot 


- 前端代码运行

> 通过 dos 窗口进入前端代码目录下，cd jx-boot/jx-vue
> 
> 运行 npm install，下载前端代码依赖库
>
> 启动项目 npm run serve
>
> 访问项目 http://localhost:8099


- 修改后端项目名和端口号 application.yml

```bash
server:
    port: 8080 //后端访问端口号
    servlet:
       context-path: /jx-boot //后端项目名
```

- 修改前端访问端口号 vue.config.js

```bash       
devServer: {
    port: 8099, //前端访问端口号
    proxy: {
      '/jx-boot': { //后端项目名
        target: 'http://localhost:8080', //后台访问地址
        ws: false,
        changeOrigin: true
      },
    }
}
```