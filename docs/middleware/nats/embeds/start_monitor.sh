#!/bin/sh
# nats server varz
/prometheus-nats-exporter -p 4001 -varz "http://embeds-2:8222"  &
/prometheus-nats-exporter -p 4002 -varz "http://embeds-3:8222"  &
/prometheus-nats-exporter -p 4003 -varz "http://embeds-1:8222"  &

# nats server connz
/prometheus-nats-exporter -p 4011 -connz "http://embeds-1:8222" &
/prometheus-nats-exporter -p 4012 -connz "http://embeds-2:8222" &
/prometheus-nats-exporter -p 4013 -connz "http://embeds-3:8222" &

# nats server routez
/prometheus-nats-exporter -p 4021 -routez "http://embeds-1:8222" &
/prometheus-nats-exporter -p 4022 -routez "http://embeds-2:8222" &
/prometheus-nats-exporter -p 4023 -routez "http://embeds-3:8222" &

# nats server subz
/prometheus-nats-exporter -p 4031 -subz "http://embeds-1:8222" &
/prometheus-nats-exporter -p 4032 -subz "http://embeds-2:8222" &
/prometheus-nats-exporter -p 4033 -subz "http://embeds-3:8222" &

# nats streaming serverz 
/prometheus-nats-exporter -p 4041 -serverz "http://embeds-1:8222"  &
/prometheus-nats-exporter -p 4042 -serverz "http://embeds-2:8222"  &
/prometheus-nats-exporter -p 4043 -serverz "http://embeds-3:8222"  &

# nats streaming channelz
/prometheus-nats-exporter -p 4051 -channelz "http://embeds-1:8222"  &
/prometheus-nats-exporter -p 4052 -channelz "http://embeds-2:8222"  &
/prometheus-nats-exporter -p 4053 -channelz "http://embeds-3:8222"  &

/bin/node_exporter
