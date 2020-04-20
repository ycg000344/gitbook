> [ycg00344.github.com](https://github.com/ycg000344/gitbook/tree/master/docs/middleware/rabbitmq)

## rabbitmq cluster + HA proxy

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