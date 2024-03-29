> [ycg000344.github.com](https://github.com/ycg000344/gitbook/tree/master/docs/middleware/etcd)

# Dockerfile

```
FROM gcr.io/etcd-development/etcd:v3.3.18
LABEL MAINTAINER="lupo@outlook.com"
```

# docker-compose

```
version: "3.6"

networks: 
    etcd-net:

volumes: 
    etcd0:
    etcd1:
    etcd2:

services: 
    etcd0:
        image: ycg000344/gcr.io-etcd-development-etcd:v3.3.18
        restart: on-failure
        networks: 
            - etcd-net
        volumes: 
            - etcd0:/etcd_data
        container_name: etcd0
        ports: 
            - "23790:2379"
            - "23800:2380"
        command: 
            - /usr/local/bin/etcd
            - --data-dir
            - /etcd_data
            - --advertise-client-urls           # 对外公告的该节点客户端监听地址，这个值会告诉集群中其他节点
            - http://etcd0:2379
            - --listen-client-urls              # 对外提供服务的地址：比如 http://ip:2379,http://127.0.0.1:2379 ，客户端会连接到这里和 etcd 交互
            - http://0.0.0.0:2379
            - --initial-advertise-peer-urls     # 该节点同伴监听地址，这个值会告诉集群中其他节点
            - http://etcd0:2380
            - --listen-peer-urls                # 监听URL，用于与其他节点通讯
            - http://0.0.0.0:2380
            - --name
            - etcd0
            - --initial-cluster
            - etcd0=http://etcd0:2380,etcd1=http://etcd1:2380,etcd2=http://etcd2:2380

    etcd1:
        image: ycg000344/gcr.io-etcd-development-etcd:v3.3.18
        restart: on-failure
        networks: 
            - etcd-net
        volumes: 
            - etcd1:/etcd_data
        container_name: etcd1
        ports: 
            - "23791:2379"
            - "23801:2380"
        command: 
            - /usr/local/bin/etcd
            - --data-dir
            - /etcd_data
            - --advertise-client-urls           # 对外公告的该节点客户端监听地址，这个值会告诉集群中其他节点
            - http://etcd1:2379
            - --listen-client-urls              # 对外提供服务的地址：比如 http://ip:2379,http://127.0.0.1:2379 ，客户端会连接到这里和 etcd 交互
            - http://0.0.0.0:2379
            - --initial-advertise-peer-urls     # 该节点同伴监听地址，这个值会告诉集群中其他节点
            - http://etcd1:2380
            - --listen-peer-urls                # 监听URL，用于与其他节点通讯
            - http://0.0.0.0:2380
            - --name
            - etcd1
            - --initial-cluster
            - etcd0=http://etcd0:2380,etcd1=http://etcd1:2380,etcd2=http://etcd2:2380

    etcd2:
        image: ycg000344/gcr.io-etcd-development-etcd:v3.3.18
        restart: on-failure
        networks: 
            - etcd-net
        volumes: 
            - etcd2:/etcd_data
        container_name: etcd2
        ports: 
            - "23792:2379"
            - "23802:2380"
        command: 
            - /usr/local/bin/etcd
            - --data-dir
            - /etcd_data
            - --advertise-client-urls           # 对外公告的该节点客户端监听地址，这个值会告诉集群中其他节点
            - http://etcd2:2379
            - --listen-client-urls              # 对外提供服务的地址：比如 http://ip:2379,http://127.0.0.1:2379 ，客户端会连接到这里和 etcd 交互
            - http://0.0.0.0:2379
            - --initial-advertise-peer-urls     # 该节点同伴监听地址，这个值会告诉集群中其他节点
            - http://etcd2:2380
            - --listen-peer-urls                # 监听URL，用于与其他节点通讯
            - http://0.0.0.0:2380
            - --name
            - etcd2
            - --initial-cluster
            - etcd0=http://etcd0:2380,etcd1=http://etcd1:2380,etcd2=http://etcd2:2380  
        
```

# Makefile

```
.PHONY:cluster
cluster: 
	docker-compose up -d --force-recreate

.PHONY:down
down: 
	docker-compose down
```