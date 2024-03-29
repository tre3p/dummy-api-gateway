<h2 align="center">
    <img src="goose-logo.png" height=500 width=500 />
    </br>
    Dummy API-Gateway
</h2>

## Badges
[![Project Checks](https://github.com/tre3p/dummy-api-gateway/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/tre3p/dummy-api-gateway/actions/workflows/ci.yml)
<a href="https://codeclimate.com/github/tre3p/dummy-api-gateway/maintainability"><img src="https://api.codeclimate.com/v1/badges/43e65c660bdb311b5058/maintainability" /></a>
<a href="https://codeclimate.com/github/tre3p/dummy-api-gateway/test_coverage"><img src="https://api.codeclimate.com/v1/badges/43e65c660bdb311b5058/test_coverage" /></a>

## Description

The project is not intended to be used in a production environment. It is a classic API-Gateway, capable of proxying HTTP requests through itself. The project is a simple implementation, that is, apart from proxying requests nothing else is implemented (at least at the moment), so feel free if you want to use this project for some testing purposes.

## Configuration

### Route Configuration

Project uses YAML configuration, which is located by default in `/src/test/resources` and named `gateway-config.yml`. This setting can be changed using the environment variables which will be described in this block.

Typically, the configuration is as follows:

```
routes:
  - target-url: http://localhost:1234/api/v1
    source-endpoint: /sample-endpoint
    request-timeout: 10
  - target-url: http://localhost:9090/api/v1
    source-endpoint: /sample-url
    request-timeout: 10
```

1) `routes` - is a YAML collection which can contain multiple elements of your configuration.
2) `target-url` - URL to which request will be sent if a request was received at `source-endpoint`
3) `request-timeout` - timeout during which the connection to target-url will be reset if no response is received.

Service supports any HTTP methods, passes the body of the request, as well as query parameters, if they were passed.

`IMPORTANT!` -  you don't need to explicitly declare query parameters in the `target-url` block. If you have a `target-url` declared as `http://localhost:8080/api/v1` in your configuration, and you want to pass query parameters - just do it! They will be passed as follows - `http://localhost:8080/api/v1?foo=bar&bar=baz`

### Environment Variables

Project uses environment variables to configure some of the parameters:

1) `DUMMY_SERVER_THREAD_COUNT` - amount of threads which will be used in web-server `ThreadPoolExecutor`. Default value - `200`.
2) `DUMMY_CONFIG_PATH` - path to configuration file with routes. Default value - `src/main/resources/gateway-config.yml`.
3) `DUMMY_SERVER_PORT` - port on which web-server will be started. Default value - `8080`.

## Usage

At the moment, there are two ways to run the service - via docker and via running jar.

### Docker

In the root of the project there is a Dockerfile, which already contains all the necessary instructions to run the project in the Docker. All you have to do is change the environment variables inside of Dockerfile, build the image and run it.

Steps:
1) Add your own routes configuration in `route-cfg.yml` file in root directory.
2) (Optional) Edit environment variables placed in Dockerfile.
3) Build the image with command:
   ```
   docker build -t treep/dummy-gateway:1.0.0 .
   ```
4) Run the image with command:
   ```
   # You need to place port which exposed in Dockerfile inside of '-p' section
   docker run -d treep/dummy-gateway:1.0.0 -p 8080:8080
   ```
   
### JAR

Steps:
1) Go to the root directory of project and execute command:
    ```
   mvn clean package
    ```
2) Go to `/target` directory and take `.jar` file
    ```
   cd target/
    ```
3) Define environment variables or use the default one.
4) Move `.jar` file to directory with configuration file or set environment variable which specifies path to configuration file.
   ```
   mv *.jar ../
   ```
5) Run the `.jar` file
   ```
   java -jar *.jar
   ```
