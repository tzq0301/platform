package main

import (
	"bufio"
	"net/http"
)

func main() {
	url := "http://localhost:8000"

	{
		resp := must(http.Get(url))
		if resp.StatusCode != http.StatusNotFound {
			panic(nil)
		}
		scanner := bufio.NewScanner(resp.Body)
		scanner.Scan()
		if scanner.Text() != "GET" {
			panic(nil)
		}
	}

	{
		resp, _ := http.Post(url, "application/json", nil)
		scanner := bufio.NewScanner(resp.Body)
		scanner.Scan()
		if scanner.Text() != `{"method":"post"}` {
			panic(nil)
		}
	}
}

func must[T any](v T, err error) T {
	if err != nil {
		panic(err)
	}
	return v
}
