# Scripts

Platform 在为 Application 创建 Docker Container 时，采用的 Docker Container 命名策略为 `am-UUID`

因此若要寻找所有通过 Platform 创建的 Docker Containers，可以根据 `am-` 前缀进行过滤：

```shell
docker ps -a | grep am-
```

## list-containers.sh

**查找**所有“通过 Platform 创建的 Docker Containers”

```shell
bash list-containers.sh
```

## rm-all-containers.sh

**暂停**并**删除**所有“通过 Platform 创建的 Docker Containers”

```shell
bash rm-all-containers.sh
```

## rm-container.sh

根据 `CONTAINER_ID` **暂停**并**删除** Docker Container

```shell
bash rm-container.sh CONTAINER_ID
```