# Yuuka
Yuuka is a lightweight build tool for compiling your Java projects in a quick and easy way. Yuuka implements the Java compiler and JAR management tool to build, manage and clean up your projects in a platform-agnostic way.

**This is a brand new repository and the first release is under development.**

## Download and run
### Requirements
* Java 11 or newer

Download the [latest release](https://github.com/spacebanana420/yuuka/releases) and run it with `java -jar yuuka.jar`.

To get started, check what commands you can run with `java -jar yuuka.jar -h`.

## Compile from source

### Compiling from source (using Yuuka)
```
yuuka package
```

### Compiling from source (using Bash)
```
mkdir build
bash build.sh
```

### Compiling from source (Unix-like shells)
```
mkdir build
javac src/*/*.java -d build
cd build
jar cfe yuuka.jar yuuka/main yuuka/*.class
cd ..
```
