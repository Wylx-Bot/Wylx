#!/usr/bin/env bash

while
  echo "Starting Wylx..."
  ./build/install/WylxBot/bin/WylxBot
  [ $? -eq 200 ]
do :; done
