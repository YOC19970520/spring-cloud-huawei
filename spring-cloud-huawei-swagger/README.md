[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.huaweicloud/spring-cloud-huawei/badge.svg)](https://search.maven.org/search?q=g:com.huaweicloud%20AND%20a:spring-cloud-huawei-dependencies) 
# 契约注册模块

本模块的主要功能是扫描所有的 `@RestController`， 生成 swagger， 然后将 swagger 内容注册到服务中心。

swagger 生成有两种模式：

* spring cloud 原生模式
  
  spring cloud 原生模式根据 spring 接口定义的规则生成 swagger， 不进行任何处理。 spring cloud
  的接口定义非常灵活，比如一个接口可以对应多个 Path， 可以对应多个 Method。 

  2020.0.x 采用springfox生成，只兼容Open API 2，2021.0.x版本采用springdoc生成，只兼容Open API 3

* 适配 servicecomb-java-chassis 模式

  这种模式在 spring cloud 原始模式生成的 swagger 基础之上，对 swagger 进行裁剪， 以使得 spring cloud
  应用能够更好的和 servicecomb-java-chassis 微服务协作。 servicecomb-java-chassis 在 REST 接口
  定义方面更加规范， 比如一个接口只能对应一个 Path， 只能对应一个 Method， 存在唯一的 operation id 等。

  2020.0.x 采用springfox生成，只兼容Open API 2，2021.0.x版本采用servicecomb的API生成，只兼容Open API 2

默认启用适配 servicecomb-java-chassis 模式， 可以通过下面的配置开关进行切换

```yaml
spring.cloud.servicecomb.swagger.enableJavaChassisAdapter: true
```
