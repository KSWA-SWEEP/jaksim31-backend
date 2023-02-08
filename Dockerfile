FROM adoptopenjdk/openjdk11

VOLUME /tmp

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java",  "-jar", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul","/app.jar"]