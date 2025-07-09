echo off
docker rm -f ms-frontend-angular
docker image rm jamarton/ms-frontend-angular
docker build -t jamarton/ms-frontend-angular .
docker run -d --name ms-frontend-angular -p 4200:80 --network microservicios jamarton/ms-frontend-angular
