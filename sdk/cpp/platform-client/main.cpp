#include <atomic>
#include <functional>
#include <iostream>
#include <string>
#include <thread>

#include <fmt/core.h>
#include <nlohmann/json.hpp>

#include "client.h"

using namespace std::chrono_literals;

void start_client_deamon(std::string client_name, std::string client_type,
                         std::vector<std::string> pub_topics, std::vector<std::string> sub_topics,
                         std::atomic_bool &isShutdown, std::function<void(PlatformClient&, std::string)> callback);

int main() {
  // pub -> app -> sub
  //   - pub:
  //     - print an integer
  //     - pass it to app
  //   - app:
  //     - receive the integer from pub
  //     - print it
  //     - add 5
  //     - pass it to sub
  //   - sub:
  //     - receive the integer from app
  //     - print it
  //

//  std::atomic_bool isShutdown{false};
//
//  fmt::print("start pub\n");
//  start_client_deamon("pub", "pub", {"pub2app"}, {}, isShutdown, [](PlatformClient &client, std::string name) {
//    int num = 10;
//    fmt::print("[{}] publish {}\n", name, num);
//    client.publish("pub2app", nlohmann::json(num));
//
//    std::this_thread::sleep_for(500ms);
//  });
//
//  fmt::print("start app\n");
//  start_client_deamon("app", "app", {"app2sub"}, {"pub2app"}, isShutdown, [](PlatformClient &client, std::string name) {
//    for (auto && message : client.listUnreadMessages()) {
//      int num = message.data.template get<int>();
//      fmt::print("[{}] receive {}\n", name, num);
//      num += 5;
//      fmt::print("[{}] publish {}\n", name, num);
//      client.publish("app2sub", nlohmann::json(num));
//    }
//
//    std::this_thread::sleep_for(500ms);
//  });
//
//  fmt::print("start sub\n");
//  start_client_deamon("sub", "sub", {}, {"app2sub"}, isShutdown, [](PlatformClient &client, std::string name) {
//    for (auto && message : client.listUnreadMessages()) {
//      int num = message.data.template get<int>();
//      fmt::print("[{}] receive {}\n", name, num);
//    }
//
//    std::this_thread::sleep_for(500ms);
//  });
//
//  std::this_thread::sleep_for(5000ms);
//
//  isShutdown = true; // program shutdown simulation
//
//  fmt::print("shutdown\n");
//
//  std::this_thread::sleep_for(1000ms); // wait for some threads' cleaning jobs

  std::atomic_bool isShutdown{false};

  // ----------------------------------------------------------------------------------

  fmt::print("start pub\n");
  start_client_deamon("pub", "pub", {"topic"}, {}, isShutdown, [](PlatformClient &client, const std::string &name) {
    int num = 10;
    fmt::print("[{}] publish {}\n", name, num);
    client.publish("topic", nlohmann::json(num));

    std::this_thread::sleep_for(500ms);
  });

  fmt::print("start sub\n");
  start_client_deamon("sub", "sub", {}, {"topic"}, isShutdown, [](PlatformClient &client, const std::string &name) {
    for (auto && message : client.listUnreadMessages()) {
      int num = message.data.template get<int>();
      fmt::print("[{}] receive {}\n", name, num);
    }

    std::this_thread::sleep_for(500ms);
  });

  // ----------------------------------------------------------------------------------

  std::this_thread::sleep_for(5000ms);

  isShutdown = true; // program shutdown simulation

  fmt::print("shutdown\n");

  std::this_thread::sleep_for(1000ms); // wait for some threads' cleaning jobs
}

void start_client_deamon(std::string client_name, std::string client_type,
                         std::vector<std::string> pub_topics, std::vector<std::string> sub_topics,
                         std::atomic_bool &isShutdown, std::function<void(PlatformClient&, std::string)> callback) {
  std::thread([&isShutdown, client_type = std::move(client_type), client_name = std::move(client_name), pub_topics = std::move(pub_topics), sub_topics = std::move(sub_topics), callback = std::move(callback)] {
    PlatformClient client{"http://localhost:8080", client_type, client_name, pub_topics, sub_topics};

    client.doRegister();
    fmt::print("[{}] register\n", client_name);

    std::thread([&client, &isShutdown, &client_name] {
      while (!isShutdown) {
        client.keepAlive();
        fmt::print("[{}] keep alive\n", client_name);
        std::this_thread::sleep_for(2000ms);
      }
    }).detach();

    while (!isShutdown) {
      callback(client, client_name);
    }

    client.unregister();
    fmt::print("[{}] unregister\n", client_name);
  }).detach();                          
}