@echo off

mkdir build

cmake -H. -Bbuild %* ^
  -DCLINGO_BUILD_WITH_PYTHON=OFF ^
  -DCLINGO_BUILD_WITH_LUA=OFF ^
  -DCLINGO_BUILD_APPS=OFF ^
  -DCLINGO_BUILD_EXAMPLES=OFF ^
  -DCLINGO_BUILD_TESTS=OFF ^
  -DCLINGO_MANAGE_RPATH=OFF ^
  -DCLINGO_BUILD_SHARED=ON

cmake --build build ^
      --config Release ^
      --target libclingo ^
      -j %NUMBER_OF_PROCESSORS%

if errorlevel 1 exit /b %ERRORLEVEL%
