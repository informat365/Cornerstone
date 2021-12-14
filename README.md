# Cornerstone
新一代项目管理工具


## 环境依赖

* 使用jdk10
* 项目采用ant管理jar包和构建项目
  
- 数据库mysql5.7, 请安装mysql

## 上手指南

- [创建数据库](docs/db/数据库初始化手册.md)


## 项目目录结构描述
```
│ Cornerstone
├─docs 文档目录
│  ├─db    	数据库文档目录
│  └─mannual    用户操作手册
├─CornerstoneBizSystem 业务系统
│  └─src  业务系统源码目录
└─CornerstoneWebSystem Web系统
   └─websrc Web系统源码目录      
```

## 编译部署

```
//开发环境打包
ant package
```