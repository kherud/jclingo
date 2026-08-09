#!/usr/bin/env bash
#
# Regenerates the raw FFM bindings in src/generated/java from the vendored clingo header.
# The generated sources are checked in; jextract never runs as part of the Maven build.
#
# Pinned tool: jextract 22 (build "jextract 22 / JDK version 22+35-2369 / LibClang 13.0.0"),
# https://download.java.net/java/early_access/jextract/22/6/
# Set JEXTRACT to the jextract binary if it is not on the PATH.
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JEXTRACT="${JEXTRACT:-jextract}"
HEADER="$ROOT/clingo/libclingo/clingo.h"
OUT="$ROOT/src/generated/java"

if [ ! -f "$HEADER" ]; then
  echo "clingo header not found at $HEADER -- check out the clingo sources first" >&2
  exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT"

"$JEXTRACT" --output "$OUT" \
            --target-package org.potassco.clingo.internal.ffi \
            --header-class-name ClingoH \
            "@$ROOT/scripts/jextract-includes.txt" \
            "$HEADER"

echo "generated $(find "$OUT" -name '*.java' | wc -l | tr -d ' ') files in $OUT"
