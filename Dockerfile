FROM  adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine

LABEL maintainer="lazaruskorir95@gmail.com"

VOLUME /tmp/logs/docker-jenkins-intergration

EXPOSE 8992

ADD  target/docker-jenkins-intergration-1.0.jar  docker-jenkins-intergration.jar

RUN /bin/sh -c 'touch /docker-jenkins-intergration.jar'

RUN echo "Africa/Nairobi" > /etc/timezone

#ENV NO_PROXY "*.safaricom.net,.safaricom.net"

ENTRYPOINT ["java","-Xmx256m","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-jenkins-intergration.jar"]