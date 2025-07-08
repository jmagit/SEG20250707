echo off
docker rm -f ms-authentication-server
docker image rm ms-authentication-server
mvnw clean package
docker build -t ms-authentication-server .
docker run -d --name ms-authentication-server -p 8091:8080 --env DISCOVERY_URL=http://ms-eureka-server:8761 --env CONFIGSRV_URL=http://ms-config-server:8888/ ms-authentication-server
docker network connect microservicios ms-authentication-server
