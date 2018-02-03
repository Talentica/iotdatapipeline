FROM java:alpine
RUN mkdir application
RUN apk --no-cache add curl
ADD ignite.jar application/ignite.jar
WORKDIR application
CMD ["java","-Xms512m","-Xmx512m","-jar","ignite.jar", "ignite"]
