image_name="tzq0301/platform-server"

if [ -z "$1" ]; then
  image_tag="latest"
else
  image_tag=$1
fi

set -e

docker login
docker build -t ${image_name}:${image_tag} .
docker push ${image_name}:${image_tag}