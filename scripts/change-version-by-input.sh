#!/bin/bash

PROJECT_FOLDER=$(
  cd "$(dirname "$0")/.."
  pwd
)

gsed -i "s/pluginVersion = .*/pluginVersion = $VERSION/" "$PROJECT_FOLDER/gradle.properties"
