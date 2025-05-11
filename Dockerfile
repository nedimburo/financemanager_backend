FROM openjdk:23
WORKDIR /app
COPY target/financemanager.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]