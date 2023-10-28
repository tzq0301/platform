#include <atomic>
#include <functional>
#include <iostream>
#include <string>
#include <thread>

#include <fmt/core.h>
#include <nlohmann/json.hpp>

#include "client.h"

using namespace std::chrono_literals;

int main() {
  std::atomic_bool isShutdown{false};

  PlatformClient client{"http://localhost:8080", "sub", "sub", {}, {"topic"}};

  client.doRegister();
  fmt::print("register\n");

  std::thread([&client, &isShutdown] {
    while (!isShutdown) {
      client.keepAlive();
      fmt::print("keep alive\n");
      std::this_thread::sleep_for(2000ms);
    }
  }).detach();

  std::thread([&client, &isShutdown] {
    while (!isShutdown) {
      for (auto && message : client.listUnreadMessages()) {
        int num = message.data.template get<int>();
        fmt::print("receive {}\n", num);
      }

      std::this_thread::sleep_for(500ms);
    }
  }).detach();

  std::string input;
  std::getline(std::cin, input);
  isShutdown = true;

  client.unregister();
  fmt::print("unregister\n");
}