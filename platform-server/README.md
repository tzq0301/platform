# Platform Server

## API

---

### Resource Management

<details>
 <summary><code>POST</code> <code><b>/platform/resource/register</b></code></summary>

#### Request Example

```json5
{
  "clientName": "xxx",               // 根据 clientName 对 service group 进行标识
  "clientType": "...",               // 用于前端绘图
  "pubTopics": ["topic1", "topic2"], // 该 client 会哪些 Topic 发消息？
  "subTopics": ["topic3"]            // 该 client 会接收哪些 Topic 的消息？
}
```

#### Response Example

```json5
{
  "clientId": "xxx" // 唯一标识当前 client 的 ID
}
```

</details>

<details>
 <summary><code>POST</code> <code><b>/platform/resource/unregister</b></code></summary>

#### Request Example

```json5
{
  "clientId": "xxx" // 唯一标识当前 client 的 ID
}
```

#### Response Example

No response. Just take status code `200` as success.

</details>

<details>
 <summary><code>GET</code> &nbsp; <code><b>/platform/resource/graph</b></code></summary>

#### Response Example

```json5
{
  "nodes": [
    {
      "clientType": "...",
      "id": "xxx", // 如果 clientType 为 topic，则将该 code 的 id 值设置为 topic.name
      "name": "yyy"
    }, 
    { /* ... */ }, 
    { /* ... */ }
  ],
  "edges": [
    {
      "from": "xxx", // id
      "to": "yyy"    // id
    }
  ]
}
```

</details>

<details>
 <summary><code>POST</code> <code><b>/platform/resource/keepAlive</b></code></summary>

#### Request Example

```json5
{
  "clientId": "xxx" // 唯一标识当前 client 的 ID
}
```

#### Response Example

No response. Just take status code `200` as success.

</details>

---

<details>
 <summary><code>POST</code> <code><b>/platform/application/create</b></code></summary>

#### Request Example

```json5
{
  "name": "xxx",           // Docker 镜像名
  "version": "yyy",        // Docker 镜像 Tag
  "healthCheckPort": 8080, // 用于检查初始化状态的端口（默认为 HTTP 端口）
  "udpPorts": [9998, 9999] // 要暴露的 UDP 端口号
}
```

#### Response Example

```json5
{
  "application": {
    "id": "xxx",     // Docker 容器 ID
    "name": "yyy",   // Docker 镜像名
    "version": "zzz" // Docker 镜像 Tag
  }
}
```

</details>

<details>
 <summary><code>POST</code> <code><b>/platform/application/destroy</b></code></summary>

#### Request Example

```json5
{
  "applicationId": "xxx" // Docker 容器 ID
}
```

#### Response Example

No response. Just take status code `200` as success.

</details>

<details>
 <summary><code>POST</code> <code><b>/platform/application/update</b></code></summary>

#### Request Example

```json5
{
  "oldApplicationId": "xxx",              // 被更新的 Application 的 ID
  "newApplicationName": "xxx",            // 新应用的 Docker 镜像名
  "newApplicationVersion": "yyy",         // 新应用的 Docker 镜像 Tag
  "newApplicationHealthCheckPort": 8080,  // 新应用的用于检查初始化状态的端口（默认为 HTTP 端口）
  "newApplicationUdpPorts": [9998, 9999], // 新应用的要暴露的 UDP 端口号
  "updateStrategy": 1                     // 更新策略：0-默认 1-滚动
}
```

#### Response Example

```json5
{
  "application": {
    "id": "xxx",     // Docker 容器 ID
    "name": "yyy",   // Docker 镜像名
    "version": "zzz" // Docker 镜像 Tag
  }
}
```

</details>

<details>
 <summary><code>GET</code> &nbsp; <code><b>/platform/application/list</b></code></summary>

#### Response Example

```json5
{
  "applications": [
    {
      "id": "xxx",     // Docker 容器 ID
      "name": "yyy",   // Docker 镜像名
      "version": "zzz" // Docker 镜像 Tag
    },
    {
      "id": "xxx",     // Docker 容器 ID
      "name": "yyy",   // Docker 镜像名
      "version": "zzz" // Docker 镜像 Tag
    },
    {
      "id": "xxx",     // Docker 容器 ID
      "name": "yyy",   // Docker 镜像名
      "version": "zzz" // Docker 镜像 Tag
    }
  ]
}
```

</details>

## Pub/Sub Design

![img/pubsub.png](img/pubsub.png)

## Resource Design

![img/resource.png](img/resource.png)

## Application Design

![img/application.png](img/application.png)

## Proxy Design

![img/proxy.png](img/proxy.png)