# Building Yuuka from source

Yuuka itself is a Yuuka project, this means that you build Yuuka using Yuuka. As an alternative, there's also a script that can build Yuuka, though it's a bit half-baked. The build steps seen below are for UNIX-like systems.

The build steps assume that you have Yuuka installed on your system. If not, then replace `yuuka` with `java -jar yuuka.jar`.

### Requirements
* Git
* Java 11 or newer installed
* POSIX-compatible shell (optional, for building using the script)

### Setting up

```
git clone https://github.com/spacebanana420/yuuka.git
cd yuuka
```

### Building Yuuka into .class files
```
yuuka build
```

### Building an executable JAR
```
yuuka package
```

### Installing Yuuka on your system
```sh
yuuka install #Requires root
```
Yuuka is then installed on your system. By default, Yuuka is installed on /usr/local/bin/ and so it requires root. If you want to specify a different installation path, use the `-ip` or `--install-path` argument:
```
yuuka install -ip /path/for/installation
```

### Using Bash
If you do not want to use Yuuka itself to build Yuuka, you can run this shell script.
```
mkdir build
bash build.sh
```
