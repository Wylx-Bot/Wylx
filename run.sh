#!/usr/bin/env bash

GIT_COMMIT=$(git describe --dirty --always --exclude '*') docker compose up
