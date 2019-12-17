# quick usage

## single

> <https://hub.docker.com/_/rabbitmq>

```shell
docker run -d --name rabbitmq -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 25672:25672 -p 15671:15671 -p 15672:15672 -p 15674:15674 -p 15670:15670 -p 15673:15673 --restart=always rabbitmq:management 
```

## cluster

> https://github.com/ycg000344/images-Outside-GWF

### rabbitmq

#### Source

[rabbitmq](https://registry.hub.docker.com/_/rabbitmq/)

#### Tag

3.8-management

### haproxy

#### Source

[haproxy](https://registry.hub.docker.com/_/haproxy)

#### Tag

1.7

## Usage

```
git clone https://github.com/ycg000344/images-Outside-GWF.git 

git checkout rabbitmq-cluster-ha-v3.8

make cluster
```