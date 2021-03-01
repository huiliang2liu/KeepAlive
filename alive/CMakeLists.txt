# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library label, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

# 指定路径
# 生成的so库在和CMakeLists.txt同级目录下的libs文件夹下
# 一定要在add_library之前设置，否则不会生效
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY
        ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})

add_library(
        # Specifies the label of the library.
        ohmoon

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/daemon.c
)

# Specifies a path to native header files.
# include_directories(src/main/cpp/include/)

find_library(
        # Defines the label of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the label of the NDK library that
        # CMake needs to locate.
        log)

# Links your native library against one or more other native libraries.
target_link_libraries(
        # Specifies the target library.
        ohmoon

        # Links the log library to the target library.
        ${log-lib})