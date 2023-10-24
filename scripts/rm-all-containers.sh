for id in $(docker ps -a | grep am- | awk '{ print $1 }')
do
  docker stop $id
  docker rm $id
done