mvn clean package
docker login
docker buildx create \
  --name multi-platform \
  --use --platform \
  linux/amd64,linux/arm64 \
  --driver docker-container || true
docker buildx build --platform linux/amd64,linux/arm64 -t tzq0301/platform-example-pub:v1 --push .
