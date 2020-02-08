# WebScrumBoard

[![Build Status](https://travis-ci.org/abondar24/WebScrumBoard.svg?branch=master)](https://travis-ci.org/abondar24/WebScrumBoard)
[![Coverage Status](https://coveralls.io/repos/github/abondar24/WebScrumBoard/badge.svg?branch=master)](https://coveralls.io/github/abondar24/WebScrumBoard?branch=master)
[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/jonashackt/spring-boot-vuejs/blob/master/LICENSE)
[![versionspringboot](https://img.shields.io/badge/springboot-2.1.7_RELEASE-brightgreen.svg)](https://github.com/spring-projects/spring-boot)
[![versionjava](https://img.shields.io/badge/jdk-11-brightgreen.svg?logo=java)](https://github.com/spring-projects/spring-boot)
[![versionvuejs](https://img.shields.io/badge/vue.js-2.6.10-brightgreen.svg?logo=vue.js)](https://vuejs.org/)
[![versionvuecli](https://img.shields.io/badge/vue_CLI-3.11.0-brightgreen.svg?logo=vue.js)](https://cli.vuejs.org/)
[![versionnodejs](https://img.shields.io/badge/nodejs-v12.9.0-brightgreen.svg?logo=node.js)](https://nodejs.org/en/)
[![versionwebpack](https://img.shields.io/badge/webpack-4.28.4-brightgreen.svg?logo=webpack)](https://webpack.js.org/)
[![versionaxios](https://img.shields.io/badge/axios-0.18.0-brightgreen.svg)](https://github.com/axios/axios)


## Idea

It is a small app for working with project in Scrum manner. Users can create projects,sprints and tasks to them.
Users can have one or serveral roles from the list:

- Developer
- QA
- Dev Ops

## What has been used

### Backend

- Spring Boot 2
- Mybatis
- Apache CXF
- Apache Camel
- FlyWay
- Postgres 9.6

### Frontend

- VueJS 2
- Bootstrap Vue

## Build and Run

### Local

```yaml
mvn clean install
 
cd base/
mvn spring-boot:run

or 

java -jar base-<ver>.jar
```

### Docker-Compose

```yaml
mvn clean install
 
cd base/

mvn clean install -Pdocker

docker-compose up
```

NOTE: docker image with nginx must be used. In dicrectory web_server dockerfile and config files are stored.

By default image abondar/wsb-srv is used. For your own image replace in docker-compose.yaml

In web_server dir
```yaml
docker compose build . -t <image tag>
```

### Kubernetes

The app was tested in with microk8s on ubuntu.

```yaml
mvn clean install
 
cd base/

mvn clean install -DskipTests -Pkube  fabric8:deploy

```

Note 1: For minikube/production kubectl must be installed and set for the remote cluster. 

Note 2: For microk8s.kubectl usage with Fabric8 certificate must be set via command below

```yaml
kubectl config set-cluster microk8s-cluster 
  --certificate-authority=/var/snap/microk8s/current/certs/ca.crt 
  --embed-certs=true 
  --server=https://127.0.0.1:16443
```

Fabric8 requires kubectl config present in /.kube/config file. After setting certificate do

```yaml
kubectl config view
```
and save to /.kube/config 

Note 3: In microk8s used default nginx-ingress controler in cloud 
nginxinx/kubernetes-ingress controller is suggested for usage. In ingress.yaml host must be set.


PS: frontend is very bad and contains some bugs
