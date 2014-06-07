# Build file for the grails blog docker image
#
# Can be build by docker using the command:
#  docker build -t charliek/blog-service .
#
# Can be run by docker using the command:
#  docker run -p 5678:5678 -p 5679:5679 -e DW_DB_URL="jdbc:postgresql://192.168.70.99:5432/blog" -it charliek/blog-service
#

FROM charliek/openjdk-jre-7
MAINTAINER charlie.knudsen@gmail.com

RUN mkdir -p /blog

ADD build/libs/blog-service-docker-shadow.jar /blog/service.jar
ADD src/main/resources/local_config.yml /blog/config.yaml

# The external host identifier
ENV HOST_IP  127.0.0.1

# Database information
ENV DB_DRIVER org.postgresql.Driver
ENV DB_USER   blog
ENV DB_PASS   blog
ENV DB_URL    jdbc:postgresql://192.168.70.99:5432/blog

# Etcd information
ENV ETCD_URL     http://127.0.0.1:4001
ENV ETCD_PUBLISH false

EXPOSE 5678 5679
ENTRYPOINT ${JAVA_HOME}/bin/java \
    -Ddw.database.driverClass=$DB_DRIVER \
    -Ddw.database.url=$DB_URL \
    -Ddw.database.user=$DB_USER \
    -Ddw.database.password=$DB_PASS \
    -Ddw.etcd.hosts=$ETCD_URL \
    -Ddw.etcd.publish.hostName=$HOST_IP \
    -Ddw.etcd.enabled=$ETCD_PUBLISH \
    -jar /blog/service.jar \
    server /blog/config.yaml
