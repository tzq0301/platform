ports="-p 8080:8080"

for((i=8200;i<=8203;i++));
do
ports="${ports} -p ${i}:${i}"
done

for((i=9090;i<=9094;i++));
do
ports="${ports} -p ${i}:${i}"
done
#
#for((i=10000;i<=10300;i++));
#do
#ports="${ports} -p ${i}:${i}"
#done

docker run --rm ${ports} tzq0301/platform-server