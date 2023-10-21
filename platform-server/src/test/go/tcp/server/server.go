package main

import (
	"bufio"
	"io"
	"net"
	"strconv"
)

func main() {
	listener := must(net.Listen("tcp", ":"+strconv.Itoa(9010)))
	// defer must0(listener.Close())

	for {
		// 接收输入流
		conn := must(listener.Accept())

		// 解析输入流
		go handler(conn)
	}
}

func handler(conn net.Conn) {
	var (
		buf = make([]byte, 1024)
		r   = bufio.NewReader(conn)
		w   = bufio.NewWriter(conn)
	)

	n, err := r.Read(buf)
	if err != nil && err != io.EOF {
		panic(err)
	}
	data := string(buf[:n])

	println(data)

	if data != "ping" {
		panic(data)
	}

	must(w.WriteString("pong"))
	must0(w.Flush())
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
