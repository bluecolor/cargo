#!/bin/bash
rm -fr dist/*
cp target/scala-2.12/cargo-assembly-0.1-SNAPSHOT.jar dist/cargo.jar
cp log.properties dist/
cp cargo.sh.template dist/
cp app.cfg.template dist/app.cfg
touch dist/cargo.sh
echo "java -Dlog4j.configuration=file:\"./log.properties\"  -jar cargo.jar $*" > dist/cargo.sh
chmod +x dist/cargo.sh