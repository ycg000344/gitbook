# Elasticsearch 从入门到放弃

> 《Elasticsearch 核心技术与实战》学习笔记
>
> https://time.geekbang.org/course/intro/197



## 基础概念

### 索引

索引是 `Elasticsearch` 存放数据的地方，可以理解为 `RDB` 的一个 `DB`。 事实是，数据被存储和索引在分片（`shards`）中，索引只是把一个或多个分片分组在一起的逻辑空间。对于程序而言，文档存储在索引（`index`）中，索引的名称必须全部为小写，不能以下划线开头，不能包含逗号。

## 文档

文档是`Elasticsearch` 中存储的实体，可以理解为 `RDB` 中 的一行数据记录。文档由字段组成，字段相当于数据库中的列。

对比 `RDB`:

```
RDB -> DB    -> Tables -> Row      -> Columns
Es  -> index -> Types  -> Document -> Field
```











## docker

### dockerfile

```dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:7.3.1
RUN bin/elasticsearch-plugin install analysis-icu
RUN bin/elasticsearch-plugin install -b https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.3.1/elasticsearch-analysis-ik-7.3.1.zip

```

### docker-compose.yml

```dockerfile
version: '3.0'

networks:
  esnet:

volumes:
  esdata01:
    driver: local
  esdata02:
    driver: local
  esdata03:
    driver: local

services:

  # kibana
  kibana:
    image: docker.elastic.co/kibana/kibana:7.3.1
    container_name: kibana
    environment:
      - SERVER_NAME=docker-kibana
      - I18N_LOCALE=zh-CN
      - ELASTICSEARCH_HOSTS=http://es01:9200
      - ELASTICSEARCH_LOGQUERIES=true
    ports:
      - 5601:5601
    networks:
      - esnet
    depends_on:
      - es01
      - es02
      - es03

  # es01
  es01:
    # image: docker.elastic.co/elasticsearch/elasticsearch:7.3.1
    build:
      context: ./myes
    container_name: es01
    environment:
      - node.name=es01
      - discovery.seed_hosts=es02,es03
      - cluster.initial_master_nodes=es01,es02,es03
      - cluster.name=docker-cluster-es
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512M"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata01:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - esnet

  # es02
  es02:
    #image: docker.elastic.co/elasticsearch/elasticsearch:7.3.1
    build:
      context: ./myes
    container_name: es02
    environment:
      - node.name=es02
      - discovery.seed_hosts=es01,es03
      - cluster.initial_master_nodes=es01,es02,es03
      - cluster.name=docker-cluster-es
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512M"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata02:/usr/share/elasticsearch/data
    networks:
      - esnet

 # es03
  es03:
    # image: docker.elastic.co/elasticsearch/elasticsearch:7.3.1
    build:
      context: ./myes
    container_name: es03
    environment:
      - node.name=es03
      - discovery.seed_hosts=es02,es01,
      - cluster.initial_master_nodes=es01,es02,es03
      - cluster.name=docker-cluster-es
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512M"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata03:/usr/share/elasticsearch/data
    networks:
      - esnet

  # cerebro
  cerebro:
    image: lmenezes/cerebro
    container_name: cerebro
    ports:
      - 9000:9000
    networks:
      - esnet
    command:
      - -Dhosts.0.host=http://es01:9200
    depends_on:
      - es01
      - es03
      - es02

```

