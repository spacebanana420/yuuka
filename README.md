# Yuuka
Yuuka is a fast and lightweight build tool for building your Java projects from the CLI in a simple way. Yuuka implements the Java compiler and JAR management tool for building and packaging while it manages and cleans up your projects in a platform-agnostic way.

Yuuka is meant to be a more lightweight and simple alternative to Gradle and Maven.

## Requirements and Download

### Requirements
* Java 11 or newer
* (optional) UNIX-like system with a POSIX-compatible shell (for installing projects in your system using `yuuka install`)

Yuuka currently requires that you have Java installed in your system (in your $PATH) to execute the Java compiler, JAR packager and Java runtime.

### Download
Download the [latest release of Yuuka here](https://github.com/spacebanana420/yuuka/releases) and run it with `java -jar yuuka.jar`.

## Documentation

For people new to Yuuka, it's recommended you read the "Getting Started" guide. It covers the most essential parts of the build tool, such as project structure, compilation, JAR files, etc.

* [Getting Started](doc/main.md)
* [Creating and running tests](doc/tests.md)
* [Building Yuuka from source](doc/build.md)
* [Frequently Asked Questions](doc/faq.md)
