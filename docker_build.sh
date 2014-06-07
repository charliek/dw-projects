#!/bin/bash -e

################################################
# Helper functions
################################################
build_container() {
    echo "Building ${CONTAINER_NAME} version $VERSION"
    docker build -t ${CONTAINER_NAME} blog-service/.
    docker tag ${CONTAINER_NAME}:latest ${CONTAINER_NAME}:${VERSION}
}

set_version() {
    VERSION=`cat gradle.properties | grep currentVersion | cut -f2 -d"=" | perl -ne 'chomp and print'`
}


################################################
# Available sub commands
################################################
sub_blog_push() {
    sub_blog
    docker push ${CONTAINER_NAME}:${VERSION}
}

sub_blog() {
    set_version
    CONTAINER_NAME="charliek/blog-service"
    gradle clean build -Prelease=true
    cp blog-service/build/libs/blog-service-${VERSION}-shadow.jar blog-service/build/libs/blog-service-docker-shadow.jar
    build_container
}

################################################
# Run the commands
################################################
subcommand=$1
case $subcommand in
    *)
        shift
        sub_${subcommand} $@
        ;;
esac
