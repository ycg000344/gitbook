package main

import "testing"

func Test_Producer(t *testing.T) {

	for i := 0; i < 10; i++ {
		// nats
		work(producer)
		// nats-streaming
		/**
		* clientID1 会引发 panic
		* consumer 使用 `clientID1` 进行连接。
		* [Relation to NATS](https://docs.nats.io/nats-streaming-concepts/relation-to-nats)
		* [Client Connections](https://docs.nats.io/nats-streaming-concepts/client-connections)
		 */
		// streamingWork(clientID1, streamingProducer)
		streamingWork(clientID2, streamingProducer)
	}
}

func Benchmark_Producer(b *testing.B) {
	for i := 0; i < b.N; i++ {
		work(producer)
		streamingWork(clientID2, streamingProducer)
	}
}
