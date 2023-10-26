# Platform Client 客户端 SDK

## 🚀 Getting Started

1. 推荐使用 JDK 17+
2. 确保文件夹下有 `mvnw` 与 `.mvn`
3. 在 Maven `pom.xml` 文件中加入以下部分：
   ```xml
   <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>17</java.version>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.release>${java.version}</maven.compiler.release>
    </properties>
   
   <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.1.0</version> <!-- 3.x.x -->
        </dependency>

        <dependency>
            <groupId>nju.ics</groupId>
            <artifactId>platform-client-spring-boot-starter</artifactId>
            <version>xxx</version> <!-- 通过该链接选择最新版即可 https://git.nju.edu.cn/TZQ/platform/-/packages?type=&orderBy=created_at&sort=desc&search[]=client&search[]=nju%2Fics%2Fplatform-client-spring-boot-starter&search[]= -->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>3.1.0</version> <!-- 3.x.x -->
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>gitlab-maven</id>
            <url>https://git.nju.edu.cn/api/v4/projects/10385/packages/maven</url>
        </repository>

        <repository>
            <id>aliyun</id>
            <url>https://maven.aliyun.com/repository/public</url>
        </repository>
    </repositories>
   ```
4. 修改启动类（包含 `main` 函数的类）：
   ```java
   @SpringBootApplication
   public class DecisionApp {
       public static void main(String[] args) {
           SpringApplication.run(DecisionApp.class, args);  // <- 将第一个参数改为“启动类”
       }
   
       // ---------------------------------------
       // 声明要 publish 的 Topics
   
       @Bean
       public Topic actor() {
           return new Topic("app-actor");
       }
   
       // ---------------------------------------
       // 注册 subscribe 的 Topics 的回调函数

       @Listener
       public static class TopicListener {
           @Resource
           @Qualifier("actor")
           Topic topicActor;
    
           @Handler(topic = "sensor1-app")
           public void handleSensor1(Message<Integer> message, Publisher publisher) {
               publisher.publish(topicActor, message.data());
           }
       }
   }
   ```
5. 在配置文件（例如 `src/main/resources/application.yml`）中加入以下配置项：
   ```yaml
   platform:
     server-url: "http://localhost:8080"
     client-name: "pub" # 用于划分“微服务组”
     client-type: "pub" # 用于前端画图
     client-port: 8200  # 用于“健康检查”（配置成 HTTP 端口即可）
   ```
6. 添加 `Dockerfile` 文件，并修改最后一行的“启动类路径”
   ```dockerfile
   FROM openjdk:17 as build
   WORKDIR /workspace/app

   COPY mvnw .
   COPY .mvn .mvn
   COPY pom.xml .
   COPY src src

   RUN ./mvnw install -DskipTests
   RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

   FROM openjdk:17
   VOLUME /tmp
   ARG DEPENDENCY=/workspace/app/target/dependency
   COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
   COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
   COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
   ENTRYPOINT ["java", "-Dplatform.server-url=http://host.docker.internal:8080", "-cp", "app:app/lib/*", "启动类所在的路径（例如）org.example.hello.WorldApplication"]
   ```
7. 开发完后，打包 Docker 镜像（将 `IMAGE_NAME:IMAGE_TAG` 替换为你自己的值，记得在 Docker Hub 上创建对应的仓库）：
   ```shell
   docker login
   docker build -t IMAGE_NAME:IMAGE_TAG .
   docker push IMAGE_NAME:IMAGE_TAG
   ```