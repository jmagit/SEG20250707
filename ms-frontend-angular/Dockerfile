### STAGE 1: Build ###
FROM node:lts-alpine AS build
WORKDIR /app
COPY . .
RUN npm ci
RUN npx ng build --output-path /app/dist

### STAGE 2: Deploy ###
FROM nginx:alpine
COPY --from=build /app/dist/browser /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/nginx.conf

#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]

# docker build -t jamarton/ms-frontend-angular .
# docker run -d --name ms-frontend-angular -p 4200:80 --network microservicios jamarton/ms-frontend-angular
