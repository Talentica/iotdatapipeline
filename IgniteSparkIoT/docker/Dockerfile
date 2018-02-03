FROM java:alpine
RUN mkdir application
RUN apk --no-cache add curl
ADD ignite.jar application/ignite.jar
ADD application.properties application/application.properties
WORKDIR application
CMD ["java","-Xms512m","-Xmx512m","-jar","ignite.jar", "ignite"]
