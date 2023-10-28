#pragma once

#include <iostream>
#include <string>
#include <utility>
#include <vector>

#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_io.hpp>
#include <boost/uuid/uuid_generators.hpp>
#include <boost/lexical_cast.hpp>
#include <fmt/core.h>
#include <nlohmann/json.hpp>

#include "libs/httplib.h"

struct Message {
  std::string id;
  std::string publisherId;
  std::string topic;
  nlohmann::json data;
  std::string createTime;

  NLOHMANN_DEFINE_TYPE_INTRUSIVE(Message, id, publisherId, topic, data, createTime);
};

class PlatformClient {
 public:
  PlatformClient(const std::string &server_url_,
                 std::string client_type_,
                 std::string client_name_,
                 std::vector<std::string> pub_topics_,
                 std::vector<std::string> sub_topics_)
      : http_client(httplib::Client(server_url_)),
        uuid_generator(boost::uuids::random_generator()),
        client_type(std::move(client_type_)),
        client_name(std::move(client_name_)),
        pub_topics(std::move(pub_topics_)) ,
        sub_topics(std::move(sub_topics_)) {}

  void doRegister() {
    const std::string body = fmt::format(R"({{ "clientName": "{}", "clientType": "{}", "pubTopics": {}, "subTopics": {} }})", this->client_name, this->client_type, nlohmann::json(this->pub_topics).dump(), nlohmann::json(this->sub_topics).dump());
    auto resp = this->http_client.Post("/platform/resource/register", body, "application/json");
    nlohmann::json data = nlohmann::json::parse(resp->body);
    this->client_id = data["clientId"];
  }

  void unregister() {
    const std::string body = fmt::format(R"({{ "clientId": "{}" }})", this->client_id);
    this->http_client.Post("/platform/resource/unregister", body, "application/json");
  }

  void keepAlive() {
    const std::string body = fmt::format(R"({{ "clientId": "{}" }})", this->client_id);
    this->http_client.Post("/platform/resource/keepAlive", body, "application/json");
  }

  void publish(const std::string &topic, const nlohmann::json &data) {
    Message message {
      .id = boost::uuids::to_string(this->uuid_generator()),
      .publisherId = this->client_id,
      .topic = topic,
      .data = data,
      .createTime = boost::posix_time::to_iso_extended_string(boost::posix_time::second_clock::local_time()),
    };

    const std::string body = fmt::format(R"({{"message": {}}})", nlohmann::json(message).dump());
    this->http_client.Post("/platform/pubsub/publish", body, "application/json");
  }

  std::vector<Message> listUnreadMessages() {
    const std::string body = fmt::format(R"({{"clientId": "{}"}})", this->client_id);
    auto resp = this->http_client.Post("/platform/pubsub/listUnreadMessages", body, "application/json");
    nlohmann::json data = nlohmann::json::parse(resp->body);
    std::vector<Message> messages = data["messages"].get<std::vector<Message>>();
    return messages;
  }

 private:
  httplib::Client http_client;
  boost::uuids::random_generator uuid_generator;

  const std::string client_type;
  const std::string client_name;
  const std::vector<std::string> pub_topics;
  const std::vector<std::string> sub_topics;

  std::string client_id;
};