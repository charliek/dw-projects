# Build file for the grails blog docker image
#
# Can be build by docker using the command:
#  docker build -t charliek/blog-service .
#
# Can be run by docker using the command:
#  sudo docker run -p 5678:5678 -p 5679:5679 -e DW_DB_URL="jdbc:mql://192.168.70.1:3306/blog" -t charliek/blog-service
#

FROM charliek/openjdk-jre-7
MAINTAINER charlie.knudsen@gmail.com

RUN mkdir -p /opt/apps/blog-service

ADD  services/blog/blog-service/build/libs/blog-service-0.0.2-SNAPSHOT-shadow.jar /opt/apps/blog-service/blog-service.jar
ADD services/blog/blog-service/src/main/resources/local_config.yml /opt/apps/blog-service/config.yaml

EXPOSE 5678 5679
ENTRYPOINT java -Ddw.database.url=$DW_DB_URL -Ddw.etcd.hosts=$ETCD_URL -Ddw.etcd.publish.hostName=$HOST_IP -jar /opt/apps/blog-service/blog-service.jar server /opt/apps/blog-service/config.yaml