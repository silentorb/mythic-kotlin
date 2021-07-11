cmake_minimum_required(VERSION 3.8)
project(mythic)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
#SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wl,--add-stdcall-alias")
#set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++")

set(BUILD_SHARED_LIBS OFF)

#include(ExternalProject)
#ExternalProject_Add(freetype_download
#  GIT_REPOSITORY https://github.com/silentorb/freetype.git
#  GIT_TAG VER-2-8-1
#  CONFIGURE_COMMAND ""
#  BUILD_COMMAND ""
#  INSTALL_COMMAND ""
#  )

add_subdirectory("freetype")

set(
  SOURCE_FILES
  src/interface.cpp
  src/font_loader.h
  src/messageBox.cpp
)

include_directories(
  "${JNI_INCLUDE_PATH}"
  "${JNI_INCLUDE_PATH}/win32"
  "freetype/include"
  "freetype/include/freetype"
)

set(BUILD_SHARED_LIBS ON)

add_library(mythic ${SOURCE_FILES})
target_link_libraries(mythic freetype)
#set_target_properties(mythic PROPERTIES PREFIX "lib")
