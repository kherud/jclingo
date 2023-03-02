![Clingo Version 5.6.2](https://img.shields.io/badge/clingo-5.6.2-informational)

# Java Bindings for Clingo: A grounder and solver for logic programs

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

# Installation

First make sure to install the Clingo shared library (see the [official repository](https://github.com/potassco/clingo/blob/master/INSTALL.md)).
- linux: libclingo.so (pre-compiled x86-64 provided)
- macos: libclingo.dylib (pre-compiled arm64 provided)
- windows: clingo.dll

You can then use this API via Maven:

```
<dependencies>
    <dependency>
        <groupId>org.potassco</groupId>
        <artifactId>clingo</artifactId>
        <version>1.0-des-rc1</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>des-releases-public</id>
        <name>denkbares Public Releases Repository</name>
        <url>https://repo.denkbares.com/releases-public/</url>
    </repository>
</repositories>
```