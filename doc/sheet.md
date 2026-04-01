# Yuuka Cheat Sheet

## File structure

* **build**: Stores the compiled project .class and .jar files
* **lib** (optional): Stores library JARs to be used by your project
* **src**: Store your project's source code here
* **test** (optional): Store source files which can be ran independently
* **build.yuuka** (optional): Configure Yuuka, change its default behavior
* **libs.yuuka** (optional): Lets you fetch library JAR files remotely

## Command cheat sheet

* init                         creates a project file structure
* build                        compiles your project
* package                      compiles your project into an executable JAR
* packagelib                   compiles your project into a library JAR
* build-native                 compiles your project into a native binary (requires GraalVM)
* run                          compiles and runs your project
* test <test file/class>       runs a test source file
* create-test <class name>     creates a test file automatically
* tests                        lists available test files
* clean                        deletes all .class files
* install                      builds and installs your program (Unix-like only)
* install <jar path>           installs an existing JAR file
* install-native               builds your program natively with GraalVM and installs it (experimental)
* uninstall <name>             uninstalls a program, removes its script and JAR file

## Important arguments
* **-m (--main) <path>**: Set the path to the main class
  * For example: `yuuka build -m yuuka/main` for the main class in `src/yuuka/main.java`
* **-r (--release) <version>**: Set the target Java version for reading and compiling your project
  * Example: `yuuka build -r 11` for Java 11
