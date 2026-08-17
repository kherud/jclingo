![Java Version 22+](https://img.shields.io/badge/Java-22%2B-informational)
![Clingo Version 5.8.2](https://img.shields.io/badge/clingo-5.8.2-informational)

# Java Bindings for [Clingo](https://github.com/potassco/clingo): A grounder and solver for logic programs

Clingo is part of the [Potassco](https://potassco.org) project for *Answer Set Programming* (ASP).
ASP offers a simple and powerful modeling language to describe combinatorial problems as *logic programs*.
The *clingo* system then takes such a logic program and computes *answer sets* representing solutions to the given problem.
To get an idea, check the [Getting Started](https://potassco.org/doc/start/) page and the [online version](https://potassco.org/clingo/run/) of clingo.

Please consult the following resources for further information:

- [**Clingo Developer Documentation**](https://github.com/potassco/clingo)
- [Clingo User Documentation](https://github.com/potassco/guide/releases)
- [Potassco Clingo Page](https://potassco.org/clingo/)

# Usage

This library binds clingo through the Java foreign function API and therefore needs **Java 22 or newer**, the
release that API became final in. From Java 24 on, the JVM warns about the native access it performs unless it is
enabled explicitly:

```
java --enable-native-access=ALL-UNNAMED ...
```

This library is available via Maven:

```xml
<dependency>
    <groupId>org.potassco</groupId>
    <artifactId>clingo</artifactId>
    <version>5.8.2.0</version>
</dependency>
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

- Linux: x86_64, arm64 (glibc 2.17 or newer)
- MacOS: x86_64, arm64 (m-series), macOS 11 or newer
- Windows: x86_64, arm64 (requires the Microsoft Visual C++ Redistributable)

## Setup Required

If your system is not in the previous list, either

- Feel free to create an issue with details about your OS and CPU.
- Compile clingo yourself (see [this](https://github.com/potassco/clingo/blob/master/INSTALL.md#build-install-and-test)) and point the JVM argument `clingo.library.path` at it, e.g. `java -Dclingo.library.path=/path/to/libclingo.so`. Without it, the library is looked up in the platform library search path.

This also applies if you get any errors of the kind:

```
Exception in thread "main" java.lang.UnsatisfiedLinkError: ...
```

## Releasing resources

`Control`, `SolveHandle`, `Backend`, `ProgramBuilder` and the AST nodes hold native resources that have to be
released explicitly - clingo does not count its references atomically, so the binding never leaves that to the
garbage collector. Use them with try with resources, or close them by hand.

Anything collected without having been closed is reported as a warning. Set `-Dclingo.leak.traces=true` to have the
allocation site of every resource recorded, which makes such a warning point at the code that created it.
