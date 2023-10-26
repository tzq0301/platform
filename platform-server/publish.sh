image_name="tzq0301/platform-server"
image_tag="latest"

set -e

docker login
docker build -t ${image_name}:${image_tag} .
docker push ${image_name}:${image_tag}