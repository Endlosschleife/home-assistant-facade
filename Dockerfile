# build jar
FROM arm32v7/maven as builder
WORKDIR /src
COPY . /src
RUN mvn clean install

# runner image
FROM arm32v7/openjdk:11.0.3-jre as runner

WORKDIR /deployments
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

COPY --from=builder /src/target/lib/* /deployments/lib/
COPY --from=builder /src/target/*-runner.jar /deployments/app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/deployments/app.jar" ]