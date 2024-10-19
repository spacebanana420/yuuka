# Yuuka
Yuuka is a lightweight build tool for compiling your Java projects from the CLI in a simple way. Yuuka implements the Java compiler and JAR management tool to build, manage and clean up your projects in a platform-agnostic way.

## Requirements
* Java 11 or newer

Yuuka currently requires that you have Java installed in your system (in your $PATH).

# Download and run
Download the [latest release](https://github.com/spacebanana420/yuuka/releases) and run it with `java -jar yuuka.jar`.

# How to use
With Yuuka, your projects should have a simple file structure such as:
```
/build        # Mandatory, to store the compiled project
/lib          # Optional, to store library JARs
/src          # Mandatory, to store source code
/test         # Optional, for writing and running tests
/build.yuuka  # Optional, for configuring your project
```

To start a new project, you can run `yuuka init` to create a new project structure.

To build your project and run it locally, you can run `yuuka build` and `yuuka run`. If you want to make an executable JAR, you can run `yuuka package`.

There are multiple CLI arguments that let you customize the build process, such as specifying the JAR name and main class (instead of guessing), setting verbose or silent terminal output, setting the target Java release for your program, etc.

For the automatic main class detection to work, it's recommended that your main file is called "main.java", otherwise you can define the main class manually either from the CLI or in `build.yuuka`.

# Compile from source

### Using Yuuka
```
yuuka package
```
If you don't have Yuuka installed in your system, run instead:
```
java -jar yuuka.jar package
```

### Using Bash
```
mkdir build
bash build.sh
```

### In a Unix-like shell
```
mkdir build
javac src/*/*.java src/*/*/*.java -d build
cd build
jar cfe yuuka.jar yuuka/main yuuka/*.class
cd ..
```
