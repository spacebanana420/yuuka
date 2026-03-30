# Frequently Asked Questions

No one actually asked anything but let's pretend they did.

### What is Yuuka?
* A build tool for Java projects, like Maven or Gradle but lighter and much simpler

### What can I do with Yuuka?
* You can build your Java projects as well as run it, run tests, create JARs, libraries, install them on your operating system and more

### Why not Maven or Gradle?
* Those build tools can feel a bit heavy and overcomplicated if you do not require fetching remote libraries or don't have niche usecases

### Why not javac and jar?
* These are barebones tools as they should be and so they lack some features and automation

### Should I use Yuuka?
If your project doesn't fetch many libraries from a repository and you don't require special setups and functionality, you can use Yuuka. To simply build a project and get it done, Yuuka is good for it.

### What systems can Yuuka run on?
* Yuuka is for the most part platform-agnostic, except for the default project installation paths (`yuuka install`). Installation functionality is also unavailable on Windows.

### How fast is Yuuka?
* Very fast, it tries to do as little as possible while still having lots of automation. It takes nearly the same time to build your project as if you ran a script that executes javac and jar.

### What does Yuuka mean?
* The name comes from Yuuka Kazami, a character from Touhou Project. Ahhh, Yuuka-sama...

### How does "yuuka install" work
* The command `yuuka install` builds your project an executable JAR (just like `yuuka package`) but then installs it so that you can run it from anywhere in your system. By default, "/usr/local/bin/" is used as installation path for Linux and BSD systems. At this path, a script is created that runs the JAR file and passes all CLI arguments to it, while the JAR itself will be stored in "/usr/local/bin/jars/".

### Why Java 11?
* I make most of my projects with Java 11 specification/compatibility in mind. Java 8 and 9 lacked certain nice standard library features that I love using which are available in Java 11, but I have no need for versions higher than that. I always try to make my projects support the oldest Java version possible without sacrificing my features, which ends up being Java 11 most of the time.

### Can I fetch libraries like npm, Cargo and Maven?
* You can somewhat fetch libraries but in a much more manual and barebones way. Remote library fetching is done only by downloading JAR files either from Maven central (under certain limitations) or from a custom URL. Recursive fetching is not supported, and so if a library depends on other libraries it will not work out of the box.
