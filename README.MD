## Summary

- [**基础工具**](#基础工具)
- [**RPC调用**](#RPC调用)
- [**分布式锁**](#分布式锁)
- [**爬虫**](#爬虫)

## 基础工具

- **基本内容**:

  主要包含Bean转换、http请求、json解析、方法重试、SPEL与aop参数解析、方法环绕日志等

## RPC调用

- **基本内容**:

  在接口中定义请求域名、地址、参数，自动生成代理类注入至spring中


- **负载均衡**:

  域名支持配置多个，自动通过轮询进行负载均衡调用请求
## 分布式锁

- **基本内容**:

  注解的形式结合spel表达式对方法进行分布式加锁，主要原理为向Redis集群节点申请锁实现RedLock，使用时需要向RedisLockAspect写入jedisList

## 爬虫

- **基本内容**:

  通过CrawlerActuator执行爬虫请求，默认使用Jsoup爬虫策略，最后利用FieldSelect注解，自动绑定爬取的html内容到返回实体属性当中
