FROM amazoncorretto:latest
COPY ./target/puzzle_engine-1.0.0.jar /opt/puzzle/

WORKDIR /opt/puzzle
EXPOSE 8081
CMD ["java", "-jar", "puzzle_engine-1.0.0.jar"]