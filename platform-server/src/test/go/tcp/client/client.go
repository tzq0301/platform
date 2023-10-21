package main

import (
	"net"
)

func main() {
	conn := must(net.Dial("tcp", "127.0.0.1:8000"))
	// defer must0(conn.Close())

	must(conn.Write([]byte("ping")))

	buff := make([]byte, 1024)
	data := string(buff[:must(conn.Read(buff))])
	if data != "pong" {
		panic(data)
	}
}

func must[T any](v T, err error) T {
	if err != nil {
		panic(err)
	}
	return v
}

func must0(err error) {
	if err != nil {
		panic(err)
	}
}
