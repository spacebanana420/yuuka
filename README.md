# Yuuka
Yuuka is a lightweight build tool for compiling your Java projects in a quick and easy way. Yuuka implements the Java compiler and JAR management tool to build, manage and clean up your projects in a platform-agnostic way.

**This is a brand new repository and the first release is under development.**

## Requirements
* Java 11 or newer

## Download and run
Download the [latest release](https://github.com/spacebanana420/yuuka/releases) and run it with `java -jar yuuka.jar`.

To get started, check what commands you can run with `java -jar yuuka.jar -h`.

### How to use
Yuuka is a simple build tool, which currently is 100% a CLI tool, making no use of build files or declarative files (for now at least).

The main tasks you can run are:
```
yuuka init
yuuka build
yuuka package
yuuka run
yuuka clean
```
To start a new project, you can run `yuuka init` to create a new project structure, although missing directories are automatically created and so this command is not necessary.

To build your project and run it locally, you can run `yuuka build` and `yuuka run`. If you want to make an executable JAR, you can run `yuuka JAR`.

There are multiple CLI arguments that let you customize the build process, such as specifying the JAR name and main class (instead of guessing), setting verbose or silent terminal output, setting the target Java release for your program, etc.

## Compile from source

### Compiling from source (using Yuuka)
```
yuuka package
```
If you don't have Yuuka installed in your system, run instead:
```
java -jar yuuka.jar package
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
