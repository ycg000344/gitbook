## Makefile

```
.PONY: down
down:
	cd env && docker-compose down

.PONY: up
up:
	cd env && docker-compose up -d --force-recreate

.PONY: run
run: normal-env
	go run main.go plugin.go

.PONY: release
release: docker-env
	go build -v -o demo0315-srv *.go

.PONY: docker-env
docker-env: vet
	go env -w CGO_ENABLED=0
	go env -w GOOS=linux
	go env -w GOARCH=amd64

.PHONY: build
build: normal-env
	go build -v .

.PONY: normal-env
normal-env: vet
	go env -u CGO_ENABLED GOOS GOARCH

.PONY: vet
vet: fmt
	go vet .

.PONY: fmt
fmt: clean
	gofmt -w .

.PONY: gogo
gogo: 
	go get github.com/gogo/protobuf/protoc-gen-gofast
	go get github.com/gogo/protobuf/protoc-gen-gogofast
	go get github.com/gogo/protobuf/protoc-gen-gogofaster
	go get github.com/gogo/protobuf/protoc-gen-gogoslick

.PHONY: proto
proto: gogo
	protoc -I=. -I=D:\gopath\pkg\mod\github.com\gogo\protobuf@v1.3.1\gogoproto --micro_out=. --gogoslick_out=. proto/demo0315/demo0315.proto
	
.PONY: clean
clean: proto

	go clean

.PHONY: test
test:
	go test -v ./... -cover

.PHONY: docker
docker:
	docker build . -t demo0315-srv:latest

```