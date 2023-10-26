#include <nlohmann/json.hpp>

#include "client.h"

int main() {
  PlatformClient client("http://localhost:8080", "app", "app", {"t1", "t2"}, {"t3", "t4", "t5"});
  client.doRegister();
  client.publish("t3", 1);
  client.publish("t4", "1");

  for (auto &&message : client.listUnreadMessages()) {
    std::cout << nlohmann::json(message) << std::endl;
  }

  client.keepAlive();
  client.unregister();
}
