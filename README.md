### Implement spring gateway woth java config

- [reference](https://spring.io/projects/spring-cloud-gateway)
- [ref example](https://www.baeldung.com/spring-cloud-gateway-url-rewriting)
- [ref](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-starter)
- [dev guide](https://cloud.spring.io/spring-cloud-gateway/reference/html/#developer-guide)

##### 
```text
For a global session_id as request path
in the mygatewayFilter, we add a K/V to exchange, than call the 
putUriTemplateVariables(exchange, Collections.singletonMap(SESSION_ID, session_id)); to add to the URL
```

#### make MVC works add to application.properties
```text
spring.main.web-application-type=reactive
```

#### Create project
```shell
http -d https://start.spring.io/starter.zip type==maven-project \
language==java \
bootVersion==2.4.5 \
baseDir==gw4 \
groupId==com.azunitech.search \
artifactId==gw4 \
name==gw4 \
packageName==com.azunitech.search \
javaVersion==1.8 \
dependencies==web,webflux,lombok
```
#### Add dependent management
```xml
  <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>io.projectreactor</groupId>
                <artifactId>reactor-bom</artifactId>
                <version>2020.0.16</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

#### Add and remove web 
```text
<spring-cloud.version>2021.0.1</spring-cloud.version>
```
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
##### for M1
```xml
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-resolver-dns-native-macos</artifactId>
        <version>4.1.90.Final</version>
        <classifier>osx-aarch_64</classifier>
    </dependency>
```

##### For X86
```xml
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-resolver-dns-native-macos</artifactId>
        <version>4.1.90.Final</version>
        <classifier>osx-x86_64</classifier>
    </dependency>
```
