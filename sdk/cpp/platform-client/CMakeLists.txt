cmake_minimum_required(VERSION 3.26)
project(platform_client)

set(CMAKE_CXX_STANDARD 23)

include(FetchContent)

FetchContent_Declare(json URL https://github.com/nlohmann/json/releases/download/v3.11.2/json.tar.xz)
FetchContent_MakeAvailable(json)

FetchContent_Declare(fmt URL https://github.com/fmtlib/fmt/archive/refs/tags/8.0.1.tar.gz)
FetchContent_MakeAvailable(fmt)

find_package(Boost REQUIRED)

add_executable(platform_client main.cpp)
target_link_libraries(platform_client PRIVATE nlohmann_json::nlohmann_json fmt Boost::boost)

add_executable(pub pub.cpp)
target_link_libraries(pub PRIVATE nlohmann_json::nlohmann_json fmt Boost::boost)

add_executable(sub sub.cpp)
target_link_libraries(sub PRIVATE nlohmann_json::nlohmann_json fmt Boost::boost)
