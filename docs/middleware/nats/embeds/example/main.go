package main

import (
	"fmt"
	"log"
	"os"
	"strings"

	nats "github.com/nats-io/nats.go"
	stan "github.com/nats-io/stan.go"
)

func main() {
	forever := make(chan bool)
	go work(consumer)
	go streamingWork(clientID1, streamingConsumer)
	<-forever
}

//
//
// nats-streaming
//
//

const (
	// clusterID        = "nats-streaming-cluster"
	clusterID        = "embeds-cluster"
	clientID1        = "c1"
	clientID2        = "c2"
	streamingSubject = "streaming-subject"
)

type streamingExec func(sc stan.Conn) error

func streamingWork(clientID string, f streamingExec) {
	/**
	* servers nats的地址
	* [doc](https://docs.nats.io/nats-streaming-concepts/relation-to-nats)
	 */
	sc, err := stan.Connect(clusterID, clientID, stan.NatsURL(strings.Join(servers, ",")))
	defer sc.Close()
	if err != nil {
		log.Fatal(err)
		os.Exit(1)
	}
	if err = f(sc); err != nil {
		log.Fatal(err)
		os.Exit(1)
	}
}

func streamingConsumer(sc stan.Conn) error {
	forever := make(chan bool)
	_, err := sc.Subscribe(streamingSubject, func(m *stan.Msg) {
		fmt.Printf("Received a message: %s\n", string(m.Data))
	}, stan.DurableName(streamingSubject))
	if err != nil {
		return err
	}
	<-forever
	return nil
}

func streamingProducer(sc stan.Conn) error {
	return sc.Publish(streamingSubject, []byte("Hello World, nats-streaming!"))
}

//
//
// nats
//
//

var servers = []string{"nats://127.0.0.1:4201", "nats://127.0.0.1:4202", "nats://127.0.0.1:4203"}

const (
	subject = "hello"
)

type exec func(nc *nats.Conn) error

func work(f exec) {
	nc, err := nats.Connect(strings.Join(servers, ","))
	defer nc.Close()
	if err != nil {
		log.Fatal(err)
		os.Exit(1)
	}
	if err = f(nc); err != nil {
		log.Fatal(err)
		os.Exit(1)
	}
}

func consumer(nc *nats.Conn) error {
	forever := make(chan bool)
	_, err := nc.Subscribe(subject, func(msg *nats.Msg) {
		fmt.Printf("Received a message: %s\n", string(msg.Data))
	})
	if err != nil {
		return err
	}
	<-forever
	return nil
}

func producer(nc *nats.Conn) error {
	return nc.Publish(subject, []byte("Hello World"))
}
