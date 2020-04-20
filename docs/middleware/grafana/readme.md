> [ycg000344.github.com](https://github.com/ycg000344/gitbook/tree/master/docs/middleware/grafana)

# docker-compose

> [ycg000344.github.com]()

```
version: "3.7"

volumes: 
    grafana-data:

services: 
            
    grafana:
        image: grafana/grafana
        container_name: grafana
        hostname: grafana
        restart: on-failure
        ports: 
            - ${GRAFANA_PORT}:3000
        environment: 
            - "GF_INSTALL_PLUGINS=grafana-clock-panel,grafana-simple-json-datasource"
        volumes: 
            - grafana-data:/var/lib/grafana

```

