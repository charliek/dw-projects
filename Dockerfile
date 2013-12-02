# Build file for the grails blog docker image
#
# Can be build by docker using the command:
#  docker build -t charliek/blog-service .
#
# Can be run by docker using the command:
#  sudo docker run -p 5678:5678 -p 5679:5679 -e DW_DB_URL="jdbc:mql://192.168.70.1:3306/blog" -t charliek/blog-service
#

FROM ubuntu:precise
MAINTAINER charlie.knudsen@gmail.com

RUN echo "deb http://archive.ubuntu.com/ubuntu precise main universe" > /etc/apt/sources.list
RUN apt-get update && apt-get -y upgrade
RUN apt-get -y install openjdk-7-jre-headless curl
RUN echo "JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/" >> /etc/environment

RUN mkdir -p /opt/apps/blog-service

ADD  services/blog/blog-service/build/libs/blog-service-0.0.2-SNAPSHOT-shadow.jar /opt/apps/blog-service/blog-service.jar
ADD services/blog/blog-service/src/main/resources/local_config.yml /opt/apps/blog-service/config.yaml

EXPOSE 5678 5679
ENTRYPOINT java -Ddw.database.url=$DW_DB_URL -jar /opt/apps/blog-service/blog-service.jar server /opt/apps/blog-service/config.yaml