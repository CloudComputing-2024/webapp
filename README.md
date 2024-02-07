# CSYE 6225 - Network Structure & Cloud Computing- Assignment #01

## Introduction

The objective of this assignment is to select a technology stack for a backend (API only) web application and to
implement health check API.

## Technology Stack

-**Programming Language:** Java

-**Relational Database:** MySQL

-**Backend Framework:** Springboot

-**ORM Framework:** Hibernate (Java)

## API Implementation

### Health Check Endpoint

The '/healthz' endpoint in `HealthCheckRestController`is designed to perform a health check of webapp application, focusing on
database connectivity,
handling unexpected request payloads and handling HTTP method not allowed exceptions.

- **Endpoint:** `@GetMapping("/healthz")`
- **Functionality:**
    - Check the database connection by executing a simple query (`SELECT 1`). If the database connection is successful, it proceeds;
      otherwise, it throws an SQL exception indicating an invalid database connection.
    - Verify that the request does not contain a query parameters. If the request contains a query parameters, it
      should return HTTP status code 400 Bad Request.
    - Verify that the request does not contain an unexpected payload. If the request contains a payload, it throws
      an `UnexpectedPayloadException`.
    - Verify that the only HTTP request method supported is GET. Making POST, PUT, DELETE, or PATCH requests should
      return `HttpRequestMethodNotSupportedException`.
- **Responses:**
    - **HTTP 200 OK:** Return if the database connection is successful and no unexpected payload is present. Includes
      headers to prevent caching and ensure content type options are correctly set.
    - **Errors:** Throw an `SQLException` if the database connection fails, indicating the service is unavailable.
    - **Errors:** Return HTTP status code 400 Bad Request if the request contains query parameters, indicating API
      request contains query parameters.
    - **Errors:** Throw an `UnexpectedPayloadException` if the request contains a payload, indicating API request
      contains a payload.
    - **Errors:** Throw an `HttpRequestMethodNotSupportedException` if HTTP request method is POST, PUT, DELETE, or
      PATCH, indicating Http request method not supported

## Global Exception Handling

The application incorporates a `GlobalExceptionHandler` class to manage exceptions across the entire application
uniformly. This class uses `@ControllerAdvice` to ensure that it catches exceptions thrown by any controller.

### Exception Handlers

- **SQL Exception Handling:** Catch `SQLException` and returns an HTTP 503 Service Unavailable status, indicating
  failure of database connectivity.
- **Unexpected Payload Exception Handling:** Catch `UnexpectedPayloadException` for requests with unexpected payloads,
  returning an HTTP 400 Bad Request status.
- **HTTP Method Not Allowed Handling:** Catch `HttpRequestMethodNotSupportedException` for requests using unsupported
  HTTP methods, returning an HTTP 405 Method Not Allowed status.

Each handler ensures that appropriate HTTP status codes are returned, along with headers to prevent caching and secure
content type handling.

## Testing the Application

### Unit and Integration Tests

Implemented comprehensive tests for healthz check endpoint using Spring's testing framework. These tests validate the
application's behavior under various scenarios, including database connectivity, handling unexpected payloads, and
ensuring that HTTP methods are restricted.

- **Test Setup:** Utilize `MockMvc` for simulating HTTP requests, `@Mock` for mocking dependencies, and `@InjectMocks`
  for injecting mocked services into the controller under test.
- **Key Test Scenarios:**
    - Database connectivity and response handling.
    - Handling of unexpected payloads with appropriate HTTP status codes.
    - Verification that unsupported HTTP methods return the correct HTTP status code.

## Instruction to Run the Application

- **Start MySQL Server:** run `sudo /usr/local/mysql-8.0.31-macos12-arm64/support-files/./mysql.server start` to start MySQL server.
- **Start Application:** Use your IDE or a command-line tool to launch the webApp application. For Maven projects,
  run `./mvnw package` `./mvnw spring-boot:run` in the terminal at project's root directory.
- **GET Request:** Once the application is running, you can access the /healthz endpoint. Use Postman or a command-line
  tool , open a new terminal tab `command + T` `curl -vvvv http://localhost:8080/healthz` to make a GET request.
- **Review the Response:**  A successful check will return an HTTP 200 OK status, indicating the application is running
  and the database connection is ok.
- **Add Parameters in Request:** run `curl -vvvv http://localhost:8080/healthz\?key=value`, a successful check will
  return an HTTP 400 Bad Request status, indicating API request contains unexpected query parameters.
- **Add Payloads in Request:**
  run `curl -vvvv -X GET -H "Content-Type: application/json" --data '{"param1":"value1", "param2":"value2"}' http://localhost:8080/healthz`,
  a successful check will return an HTTP 400 Bad Request status, indicating API request contains unexpected payloads.
- **POST Request:**
  run `curl -vvvv -X POST -H "Content-Type: application/json" --data '{"param1":"value1", "param2":"value2"}' http://localhost:8080/healthz`,
  a successful check will return an HTTP 400 Bad Request status, indicating HTTP Method Not Allowed.
- **PUT Request:**
  run `curl -vvvv -X PUT -H "Content-Type: application/json" --data '{"param1":"value1", "param2":"value2"}' http://localhost:8080/healthz`,
  a successful check will return an HTTP 400 Bad Request status, indicating HTTP Method Not Allowed.
- **DELETE Request:** run `curl -vvvv -X DELETE http://localhost:8080/healthz`, a successful check will return an HTTP
  400 Bad Request status, indicating HTTP Method Not Allowed.
- **PATCH Request:** run `curl -vvvv -X PATCH -H "Content-Type: application/json" -d '{"key":"newValue"}' http://localhost:8080/healthz`, a successful check will return an HTTP 400 Bad Request status, indicating HTTP Method Not Allowed.
- **Shut Down MySQL server:** run `sudo /usr/local/mysql-8.0.31-macos12-arm64/bin/mysqladmin -u root --port 3306 shutdown -p` to shut MySQL server.
- **GET Request:** Once the application is running, you can access the /healthz endpoint. Use Postman or a command-line
  tool `curl -vvvv http://localhost:8080/healthz` to make a GET request.
- **Review the Response:**  A successful check will return an HTTP 503 Service Unavailable status, indicating the
  application database connection is lost.
- **Start MySQL Server:** run `sudo /usr/local/mysql-8.0.31-macos12-arm64/support-files/./mysql.server start` to start MySQL server again.
- **GET Request:** Once the application is running, you can access the /healthz endpoint. Use Postman or a command-line
  tool `curl -vvvv http://localhost:8080/healthz` to make a GET request.
- **Review the Response:**  A successful check will return an HTTP 200 OK status, indicating the application is running
  and the database connection is ok.
