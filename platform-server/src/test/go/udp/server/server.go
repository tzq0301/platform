package main

import (
	"fmt"
	"net"
	"time"
)

func main() {
	listen := must(net.ListenUDP("udp", &net.UDPAddr{
		IP:   net.IPv4(0, 0, 0, 0),
		Port: 7000,
	}))

	for {
		var data [1024]byte
		n, addr, err := listen.ReadFromUDP(data[:]) // 接收数据
		fmt.Printf("%v\n", addr)
		if err != nil {
			fmt.Println("read udp failed, err:", err)
			continue
		}
		fmt.Printf("data:%v addr:%v count:%v\n", string(data[:n]), addr, n)

		go func() {
			must(listen.WriteToUDP(data[:n], addr))

			ticker := time.NewTicker(2 * time.Second)
			for range ticker.C {
				must(listen.WriteToUDP(data[:n], addr))
			}
			ticker.Stop()
		}()
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
