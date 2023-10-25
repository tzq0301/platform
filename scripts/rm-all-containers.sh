echo 'Are you sure? (Enter: y/N) '

read sure

if [ $sure != "y" ]; then
  exit 0
fi

echo 'removing all containers created by platform...'

for id in $(docker ps -a | grep am- | awk '{ print $1 }')
do
  docker stop $id
  docker rm $id
done

echo 'done.'