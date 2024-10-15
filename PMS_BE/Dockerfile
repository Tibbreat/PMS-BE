FROM openjdk:22

WORKDIR /app

# Sao chép file JAR từ thư mục target vào trong container
COPY ./out/artifacts/PMS_BE_jar/*.jar /app/PMS_BE.jar

# Expose port 8080 để ứng dụng có thể chạy trên cổng này
EXPOSE 8080

# Chạy file JAR đã được copy vào container
CMD ["java", "-jar", "PMS_BE.jar"]
