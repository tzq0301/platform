# 保存 Docker 镜像为 tar 文件 & 从 tar 文件 load 镜像到本地

1. 在有网的环境下，到 [tzq0301/platform-server](https://hub.docker.com/repository/docker/tzq0301/platform-server/tags?page=1&ordering=last_updated) 远程镜像仓库中选择一个版本（例如 `1.2.0`）
2. 拉取镜像（将 `TAG_YOU_CHOOSE` 替换成在第一步中选择的镜像）
   ```shell
   docker pull tzq0301/platform-server:TAG_YOU_CHOOSE
   ```
3. 将第二步中 pull 的镜像保存为 tar 文件（将 `FILENAME` 改为指定的文件名、`TAG_YOU_CHOOSE` 替换成在第一步中选择的镜像）
   ```shell
   docker save -o FILENAME.tar tzq0301/platform-server:TAG_YOU_CHOOSE
   ```
4. 将第三步得到的 tar 文件通过某种方式传输到另一台电脑中
5. 在另一台电脑中从 tar 文件 load 出镜像（将 `FILENAME` 改为你指定的文件名）
   ```shell
   docker load -i FILENAME.tar
   ```
6. 运行 `docker images | grep platform-server` 能看到输出即为 load 成功
7. 使用 `docker run --rm tzq0301/platform-server:TAG_YOU_CHOOSE` 命令即可运行平台的 Server 端程序（将 `TAG_YOU_CHOOSE` 替换成在第一步中选择的镜像） 