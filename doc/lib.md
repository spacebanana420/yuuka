# Adding libraries to your project

With Yuuka, there are 2 main ways to import a library into your project: source and JAR.

### Source library

You can import a library's source code directly into "src" as if it was part of your project's source code. Remember to also include the LICENSE file as well, all license files found in "src" are added to the JAR file you create so you can have multi-license projects.

### JAR library

If you want to import a JAR as a library, add it to "lib" and Yuuka will include it in compilation and packaging. When building your project into a JAR, the class files of that library JAR you imported will also be included inside.

### Fetching libraries

You can fetch libraries by configuring the libs.yuuka file. This is a barebones and experimental implementation and does not recursively fetch libraries for cases where a library depends on other ones. You can fetch from the Maven central (with limitations) or with a custom URL.
