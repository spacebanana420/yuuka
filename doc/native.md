# Building a native executable

Yuuka has built-in support for GraalVM's `native-image` binary, which provides the capability to compile a native executable out of a Java program instead of JVM bytecode and JAR files. The result is a program that runs faster, uses much less RAM and starts up almost instantly (5-7ms instead of 30-40ms).

Yuuka's implementation of `native-image` doesn't go very deep and is somewhat experimental, but covers the needs for building static and non-static native executables.

## Setup

The steps below assume that you are on Linux, but GraalVM does also work on Windows and MacOS.

* Download the latest or preferred [GraalVM version](https://www.graalvm.org/downloads/)
* Create a symlink to the `native-image` binary and place it somewhere in your $PATH (e.g /usr/local/bin/), or alternatively specify the path to the binary on Yuuka using build.yuuka or the `-gp` or `--graal-path` argument.
* Install `gcc`, `zlib`, `glibc-devel` and `libstdc++`.
  * Example: for Arch Linux, EndeavourOS and Artix, run as root `pacman -S gcc zlib libstdc++`
  * Note: extra dependencies are needed for building fully static binaries with musl, such as `libstdc++-static`

For full instructions, see the official documentation:

* [Setting up and using native-image](https://www.graalvm.org/latest/reference-manual/native-image/#build-a-native-executable-using-the-native-image-tool)
* [Build a native binary from a JAR](https://www.graalvm.org/latest/reference-manual/native-image/guides/build-native-executable-from-jar/)
* [Build a static binary](https://www.graalvm.org/latest/reference-manual/native-image/guides/build-static-executables/)

## Building a native binary

After setting up GraalVM's native-image and required dependencies, run `yuuka build-native` to build a binary. Running `yuuka package` or `yuuka build` before is not necessary, these steps are covered in this one command automatically.

Yuuka currently always passes the `-O3` compilation argument to native-image, which is one of the ideal compiler options for release builds.

### Dynamic and static binaries

* By default, Yuuka builds a binary which is dynamically-linked to glibc, zlib and possibly more native system libraries.
* To build a mostly-static binary (only dynamically-linked to system glibc), run `yuuka build-native --static-nolibc`.
* To build a fully-static binary, which has **musl libc** included in it, run `yuuka build-native --static`.
  * Building fully-static binary requires `musl`, `libstdc++-static` and possibly `musl-static`.
  
## Installing the program on your system

Just like you can run `yuuka install` to install your program on your system, Yuuka has the native counterpart `yuuka install-native`, which instead builds and installs a native binary.

## Known limitations

* GraalVM is currently not available for any BSD family operating system or less popular systems such as Haiku or Plan9
* GraalVM is not available for many CPU architectures, mostly being focused on x86_64 and ARM 64
* Linux systems that use musl C library instead of glibc (such as Alpine Linux) cannot make use of GraalVM
* Native binaries might introduce certain bugs and quirks, such as `java.io.file.File.list()` not including paths with filenames that are not encoded in UTF-8
