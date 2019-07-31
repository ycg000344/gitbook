# quick usage

## MasterSlave

### start container

```shell
# master
docker run --name mysql-master --privileged=true -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -v {docker/host/master/volume}:/var/lib/mysql -d mysql:5.7
```

```shell
# slave
docker run --name mysql-slave --privileged=true  -p 3307:3306 --link mysql-master:master -e MYSQL_ROOT_PASSWORD=root -v {docker/host/salve/volume}:/var/lib/mysql -d mysql:5.7
```

### operate

````shell
# master
docker exec -it mysql-master mysql -uroot -proot

grant replication slave on *.* to 'test'@'%' identified by '123456';

flush privileges;

# 查看主的状态, 注意 mysql-bin 文件名称和 position
show master status;
````

```shell
# slave
docker exec -it mysql-slave mysql -uroot -proot

# 注意 mysql-bin 文件名称和 position
change master to master_host='master', master_user='test', master_password='123456', \
master_port=3306, master_log_file='mysql-bin.000003', master_log_pos=589, master_connect_retry=30;

start slave;
// 查看从的状态
show slave status\G
```

