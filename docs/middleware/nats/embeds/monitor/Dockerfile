FROM prom/node-exporter
LABEL MAINTAINER="lupo.f@outlook.com"
COPY ./prometheus-nats-exporter /prometheus-nats-exporter
COPY ./start_monitor.sh /start_monitor.sh
ENTRYPOINT [ "/start_monitor.sh" ]

#更新Alpine的软件源为国内（清华大学）的站点，因为从默认官源拉取实在太慢了。。。
# RUN echo "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main/" > /etc/apk/repositories
# RUN apk update \
#         && apk upgrade \
#         && apk add --no-cache bash \
#         bash-doc \
#         bash-completion wget unzip \
#         && rm -rf /var/cache/apk/* \
#         && /bin/bash