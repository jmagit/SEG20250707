echo off
docker rm -f ms-catalogo
docker image rm ms-catalogo
mvnw clean package
docker build -t ms-catalogo .
docker run -d --name ms-catalogo -p 8010:8080 --env DB_SERVER=mysql-sakila:3306 --env DISCOVERY_URL=http://ms-eureka-server:8761 --env CONFIGSRV_URL=http://ms-config-server:8888/ --env ZIPKINSRV_URL=http://zipkin:9411 --network microservicios ms-catalogo
docker network connect microservicios ms-catalogo
