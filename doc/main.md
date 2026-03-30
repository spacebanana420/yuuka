# Getting Started with Yuuka
Yuuka is a fast and lightweight build tool for building your Java projects from the CLI in a simple way. Yuuka implements the Java compiler and JAR management tool for building and packaging while it manages and cleans up your projects in a platform-agnostic way. Yuuka was made with simplicity and speed in mind, prioritizing offline environments that don't fetch dependencies remotely, and sits much closer to barebones javac setups than big tools like Maven or Gradle.

Yuuka is a CLI tool and only requires JDK 11 or newer installed in your system to function.

## Creating a project
Yuuka projects follow a simple but necessary project structure. Your projects should have a simple file structure such as:
```
/build        # Mandatory, to store the compiled project
/lib          # Optional, to store library JARs
/src          # Mandatory, to store source code
/test         # Optional, for writing and running tests
/build.yuuka  # Optional, for configuring your project
/libs.yuuka   # Optional, for fetching remote libraries
```

To start a new project, you can run `yuuka init` to create a new project structure like this one.

## Configuring a project

You can control Yuuka's behavior by using its CLI arguments, or by modifying the values in build.yuuka. It's highly recommended that from build.yuuka you set the path to your main class (if it exists).

You can assign your program's main class with the `main_class` setting. This setting follows a `path/to/main` approach. For example, Yuuka itself has its main class located at "yuuka/main.java", and main.java belongs to the "yuuka" package. This means that, the path to the main class is `yuuka/main`.

If you do not specify the main class's path from build.yuuka or from the CLI, Yuuka will try to guess where your project's main class is by looking for a file named `main.java`. This is a barebones approach and prone to error if you happen to have multiple main.java files or they are not meant to represent your project's starting point, and so it's recommended you specify the path yourself.

## Building your project

After configuring your project, you can build it with `yuuka build`. This compiles your program into .class bytecode files located in "build" directory. If you specify a starting point, you can execute these files right away as you would if you compiled with javac.

## Building an executable JAR

Class files are nice for direct control, but you will often want to build a JAR file which you can execute as a Java program. You can compile your project into a JAR file using `yuuka package`. The compilation step is included in this command, you don't have to run `yuuka build` prior. Creating an executable JAR requires that your main class is assigned or found.

## Building a library JAR

A library JAR is a JAR file which is not executable, has no starting point. These JARs are extremely useful for compiling library code that can then be imported into other projects. Library JARs do not need a main class. You create a library JAR by running `yuuka packagelib`. This step does not require a prior `yuuka build` execution, just like the executable JAR counterpart.

## Running your program directly

It's convenient to run your project directly without thinking about compilation, you can do this with `yuuka run`. You can pass command-line arguments to your program with the `--` argument. Any CLI argument after this one is passed into your program, for example, `yuuka run -- --help` passes the `--help` argument to your program.

## The help screen

Open Yuuka's help screen by running `yuuka -h` or `yuuka --help`. From here, you can see everything you can do with Yuuka.

## Beware the build

Depending on what you do, Yuuka can clean up clutter files in your project, such as outdated class and JAR files. In some cases, all files inside "build" might be deleted, so be careful with what you put there.
