#!/bin/bash

mkdir -p build

cmake -H. -Bbuild $@ \
  -DCLINGO_BUILD_WITH_PYTHON=OFF \
  -DCLINGO_BUILD_WITH_LUA=OFF \
  -DCLINGO_BUILD_APPS=OFF \
  -DCLINGO_BUILD_EXAMPLES=OFF \
  -DCLINGO_BUILD_TESTS=OFF \
  -DCLINGO_MANAGE_RPATH=OFF \
  -DCLINGO_BUILD_SHARED=ON \
  || exit 1

cmake --build build \
      --config Release \
      --target libclingo \
      -j $(nproc) \
      || exit 1

ls -l
ls -l build
ls -l build/bin