FROM openjdk:8-jre-alpine
COPY src/test/resources /jservRoot
COPY src/main/resources/server.properties /jserv.properties
COPY src/main/resources/logging.properties /logging.properties
COPY target/jserv.jar /jserv.jar


EXPOSE 8080
ENTRYPOINT java -server -Xms512M -Xmx512M -XX:+UseG1GC -Djava.util.logging.config.file=/logging.properties -jar /jserv.jar "/jserv.properties"