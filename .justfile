test:
    mvn clean test

build:
    mvn clean package

deploy:
    mvn clean package deploy

run-jar:
    #!/usr/bin/env -S bash
    if [ ! -f target/jais.jar ]; then
        mvn clean package -DskipTest
    fi
    java -jar target/jais.jar jais.Application
