# Platform SDK for C++

C++ SDK 为 `sdk/cpp/platform-client/client.h`（header only）

SDK 依赖 `nlohmann/json`、`fmtlib/fmt`、`boost`、`httplib` 库

SDK 包含 `PlatformClient` 类，其负责通过 HTTP 网络协议与 Platform 服务端进行交互

## PlatformClient 构造函数

构造函数负责对 `PlatformClient` 进行初始化，其中：

- `server_url` 是 Platform 服务端的 URL 地址（例如 `http://localhost:8080`）
- `client_type` 是“客户端的类型”，主要用于前端绘图，不会用于服务端的处理逻辑
- `client_name` 是“客户端的服务名”，相同 `client_name` 的一系列服务将被视为同一个服务的不同版本
- `pub_topics` 包含了“客户端将 publish 消息的 Topic”，主要用于前端绘制拓扑图，即使不传也不会影响服务端的逻辑
- `sub_topics` 包含了“客户端订阅的 Topic

```cpp
PlatformClient(const std::string &server_url_,
               std::string client_type_,
               std::string client_name_,
               std::vector<std::string> pub_topics_,
               std::vector<std::string> sub_topics_) 
```

## Register 注册

主动向服务端发起注册的方法为 `doRegister`，其将自己的信息传递给服务端以进行注册，并得到服务端返回的一个全局唯一的 Client ID：

```cpp
void doRegister();
```

## Unregister 注销

主动向服务端进行注销的方法为 `doRegister`

如果客户端因为停电等意外而未能调用该方法也没关系，因为服务端会定期进行心跳检测，当服务端检测到客户端在一段时间里没有进行 `keepAlive `请求时，会主动注销该客户端在服务端中存储的信息

```cpp
void unregister();
```

## KeepAlive 心跳

客户端需要定期向服务端发起 `keepAlive` 请求以维持自身“活性”

```cpp
void keepAlive()
```

在后台线程中每三秒向服务端发起一次请求的伪代码如下：

```cpp
void keepAliveTask {
    while (true) {
        client.keepAlive();
        std::this_thread::sleep_for(std::chrono::seconds(3));
    }
}

std::thread t(keepAliveTask);
t.detach();
```

## Publish 消息推送

客户端主动向指定 Topic 发起消息推送，数据需要转换为 `nlohmann::json` 类型的数据

```cpp
void publish(const std::string &topic, const nlohmann::json &data);
```

## ListUnreadMessages 获取新消息

客户端通过 `listUnreadMessages` 方法来获取所有自己订阅的 Topic 中新来的消息

```cpp
std::vector<Message> listUnreadMessages();
```

### Demo

```cpp
#include <nlohmann/json.hpp>

#include "client.h"

int main() {
  PlatformClient client("http://localhost:8080", "app", "app", {"t1", "t2"}, {"t3", "t4", "t5"});
  client.doRegister();
  client.publish("t3", 1);
  client.publish("t4", "1");

  for (auto &&message : client.listUnreadMessages()) {
    std::cout << nlohmann::json(message) << std::endl;
  }

  client.keepAlive();
  client.unregister();
}
```