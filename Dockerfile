FROM maven:3-openjdk-15-slim AS build
WORKDIR /app
COPY . .
RUN mvn -B package

FROM tomcat:10-jdk15-openjdk-slim
COPY --from=build /app/target/webservice-1.0.war $CATALINA_HOME/webapps/ROOT.war
EXPOSE 8080
