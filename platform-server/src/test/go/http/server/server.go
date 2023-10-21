package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strconv"
)

func main() {
	port, _ := strconv.Atoi(os.Args[1])

	_ = http.ListenAndServe(fmt.Sprintf(":%d", port), http.HandlerFunc(func(w http.ResponseWriter, request *http.Request) {
		println(port)

		switch request.Method {
		case http.MethodGet:
			w.WriteHeader(http.StatusNotFound)
			_, _ = w.Write([]byte("GET"))
		case http.MethodPost:
			data, _ := json.Marshal(map[string]string{
				"method": "post",
			})
			_, _ = w.Write(data)
		default:
			panic(nil)
		}
	}))
}
