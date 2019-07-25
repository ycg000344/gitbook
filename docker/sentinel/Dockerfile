FROM openjdk:8

RUN mkdir /apps

WORKDIR /apps

COPY ./sentinel-dashboard.jar ./sentinel-dashboard.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-Dserver.port=8080", "-Dcsp.sentinel.dashboard.server=localhost:8080 ", "-Dproject.name=sentinel-dashboard ","-jar", "./sentinel-dashboard.jar" ]