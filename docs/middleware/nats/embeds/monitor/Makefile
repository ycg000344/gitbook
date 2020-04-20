
.PONY: build
build: 
	docker build -t ycg000344/prometheus-nats-exporter:0.6.0  --no-cache .

.PONY: run
run:
	docker run --rm --name prometheus-nats-exporter ycg000344/prometheus-nats-exporter:0.6.0

.PONY: rmi
rmi:
	docker stop prometheus-nats-exporter || docker rmi -f ycg000344/prometheus-nats-exporter:0.6.0