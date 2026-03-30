# Creating and running tests

With Yuuka, you can run Java source files or executable files located in "test". This is very convenient for placing quick tests there without affecting your real source code. These tests can be isolated, or they can import your own project's source code and use it.

### Creating a test

Run `yuuka create-test <class name>` to create a Java test file, replacing "<class name>" with the name of the class. This will create a Java source file located in "tests".

### Running a test

Run `yuuka test <class name>` to run a test source file whose public class is of name "<class name>".

### Having your tests import your project

If you want your tests to access the code and functionality from your project, run Yuuka with the `-is` or `--include-src` argument. You can also configure this option from build.yuuka instead.
