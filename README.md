# 本插件不再更新！！！
由于这个插件的几个功能依赖了jrebel，所以不再更新了（193.5）。所有功能已经拆成了单独的插件，且不依赖jrebel，更好用了。

本人插件主页，欢迎拍砖：https://plugins.jetbrains.com/author/6c08090c-6cc3-4b01-94a2-2c9a9947b6cd

- 快速打开配置中心 https://plugins.jetbrains.com/plugin/13202-apollo-conf

- MySQL EXPLAIN https://plugins.jetbrains.com/plugin/13192-mysql-explain

- 快速调用 https://plugins.jetbrains.com/plugin/13203-bean-invoker

- jvm启动参数管理 https://plugins.jetbrains.com/plugin/13204-jvm-parameter

- 快速打开git仓库 暂时没上传

---

# quick-dev-intellij-plugin

快速开发插件-idea，主要包含功能：
- 快速打开配置中心
- 快速打开git仓库
- MySQL EXPLAIN
- 快速调用
- jvm启动参数管理


## 下载地址

[jetbrains插件仓库](https://plugins.jetbrains.com/plugin/13035-quick-dev)

## 使用说明

### 插件配置说明

本着约定优于配置的理念，所有配置非必须，针对某些特殊需求可自行设置。

JR插件默认启用，此配置控制是否启用MySQL EXPLAIN和快速调用

- 入口1: `Tools/Quick Dev/Configure Plugin`
- 入口2: 右键项目树，找到`Quick Dev/Configure Plugin`
    
### 快速打开配置中心

- 自动读取项目中的`resources/META-INF/app.properties`，并跳转到Apollo页面
- 入口：`Tools/Quick Dev/Open Apollo`

### 快速打开git地址

- 读取项目git信息，并打开相应页面
- 入口：`Tools/Quick Dev/Open Gitlab`

### MySQL EXPLAIN

- 自动输出执行的SQL语句
- 自动输出SQL语句的执行计划
- 以上两者不重复，可设置过滤条件
- 相关配置描述如下：

```properties
# 非必填项：是否打印所有执行的MySQL语句，默认false。设置true时，会根据[mysql.filter]过滤滤
mysql.showSQL=false

# 非必填项：MySQL explain执行过滤，按关键词匹配，英文逗号分割，比如：QRTZ_,COUNT(0)
mysql.filter=QRTZ_,COUNT(0)

# 非必填项：MySQL explain结果按[type]过滤，默认ALL，英文逗号分割，*打印所有
mysql.types=ALL

# 非必填项：MySQL explain结果按[Extra]过滤，默认Using filesort,Using temporary，英文逗号分割，*打印所有
mysql.extras=Using filesort,Using temporary
```

### 快速调用

- 需指定**无参方public法**，支持自动生成。此方法中，可实现其他有参函数调
- 入口：当前方法右键，找到`快速调用`，默认快捷键`alt x`

### jvm参数管理

添加自定义参数，如：-Xms512m，在项目启动时会自动设置到jvm启动参数中。
