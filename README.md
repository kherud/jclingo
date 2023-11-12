![Clingo Version 5.6.2](https://img.shields.io/badge/clingo-5.6.2-informational)

# Java Bindings for [Clingo](https://github.com/potassco/clingo): A grounder and solver for logic programs

**This non-official repository contains Java bindings for Clingo.**

Clingo is part of the [Potassco](https://potassco.org) project for *Answer Set
Programming* (ASP).  ASP offers a simple and powerful modeling language to
describe combinatorial problems as *logic programs*.  The *clingo* system then
takes such a logic program and computes *answer sets* representing solutions to
the given problem.  To get an idea, check our [Getting
Started](https://potassco.org/doc/start/) page and the [online
version](https://potassco.org/clingo/run/) of clingo.

Please consult the following resources for further information:

- [**Downloading source and binary releases**](https://github.com/potassco/clingo/releases)
- [**Installation and software requirements**](https://github.com/potassco/clingo/blob/master/INSTALL.md)
- [Documentation](https://github.com/potassco/guide/releases)
- [Potassco clingo page](https://potassco.org/clingo/)

# Usage

This library is available via GitHub packages:

```xml
<dependencies>
    <dependency>
        <groupId>org.potassco</groupId>
        <artifactId>clingo</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/kherud/jclingo</url>
    </repository>
</repositories>
```

Please have a look at the [demonstrations](src/test/java/demo), for example:

```java
public class Main {
    public static void main(String... args) {
        Control control = new Control("0");

        // add a logic program to the base part
        control.add("a :- not b. b :- not a.");

        // ground the base part
        control.ground();

        try (SolveHandle handle = control.solve(SolveMode.YIELD)) {
            while (handle.hasNext()) {
                // get the next model
                Model model = handle.next();
                System.out.println(model);
            }
        }

        control.close();
    }
}
```

## No Setup Required

This repository provides out of the box support for the following platforms:

- Linux: x86_64, arm64 (glibc)
- MacOS: x86_64, arm64 (m1)
- Windows: x86, x86_64, arm, arm64

## Setup Required

If your system is not in the previous list, either

- Feel free to create an issue with details about your OS and CPU
- Compile clingo yourself (see [this](https://github.com/potassco/clingo/blob/master/INSTALL.md#build-install-and-test)) and appropriately set the JVM argument `jna.library.path`, e.g. `java -Djna.library.path=/path/to/library`.

This also applies if you get any errors of the kind:

```
Exception in thread "main" java.lang.UnsatisfiedLinkError: ...
```