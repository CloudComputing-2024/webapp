test
# Webapp

## Introduction

The webapp, developed in Java and utilizing the Spring Boot framework with a REST architecture, employs Spring JPA for
database initialization and Hibernate for data management. Hosted on a CentOS 8 AWS EC2 instance server, it utilizes MySQL 8.0.31
for data storage. Application security is enhanced with Spring Security, implementing Basic authentication to manage
user information access.

## Technology Stack

-**Programming Language:** Java

-**JDK:** open JDK 17

-**Build Tool**: Maven

-**Relational Database:** MySQL 8.0.31 (Arm64)

-**Backend Framework:** Springboot

-**ORM Framework:** Hibernate (Java)

-**Server**: CentOS 8 (AWS EC2 instance)

## Build Instruction

### Pre-Requisites:
- **Install Java JDK 17**

- **Install Maven**

- **Install MySQL 8.0.31 and set up database environment**

- **Install postman**

### Steps to Build

- **Clone this repository into the local system** `git clone git@github.com:CloudComputing-2024/webapp.git`

- **Start MySQL Server:** run `sudo systemctl start mysqld.service` to start
  MySQL server.

- **Build the project:** Use your IDE or a command-line tool to launch the webApp application. For Maven projects,
  run `./mvnw package -DskipTests` `./mvnw spring-boot:run`

## Deploy Instruction

### Prepare the Server
- **Ensure your AWS EC2 instance running CentOS 8 is set up and running**
- **Install Java JDK 17 on the server if it's not already installed**
- **Install MySQL 8.0.31 and set up database environment**

### Transfer the Executable Zip
- **Use SCP to transfer the built zip file from local machine to the server** `scp -i /Users/amy/Downloads/Test.pem webapp.zip centos@ec2-44-222-200-64.compute-1.amazonaws.com:/home/centos`

### Run the application
- **SSH into your EC2 instance and navigate to the directory where you've transferred the zip file**
- **Unzip the file** run `sudo yum install unzip`
- **Start the sql server**: run `sudo systemctl start mysqld.service`
- **Run the application**: `./mvnw package -DskipTests` `./mvnw spring-boot:run`
- **Configure Security**: set up security groups in AWS to only allow traffic on necessary ports(like 8080)
- **Test endpoints in Postman**

### Build Custom Application Images with Packer
- **Base Image**: Centos Stream 8
- **Project and Network Configuration**: default VPC
- **Application Dependencies**: `Java SDK 17` `MySQL 8.0.31` `Maven` `Unzip`
- **Create Local User**: A nologin user `csye6225` was created

### GitHub Actions for Continuous Integration
#### Workflow for Status Check
- Runs `packer fmt` to check the format of the Packer template**
- Runs `packer validate` to validate the Packer template

#### Workflow to Build Custom Images ####
- Created a new IAM service account in the DEV GCP project for GitHub Actions, configuring security credentials appropriately.
- Assigned necessary roles to the service account following GCP documentation and installed & configured the gcloud CLI on GitHub Actions runner.
- On pull request merge:
  - Conducted integration testing.
  - Run  `mvn clean package` to build application artifacts on the GitHub Actions runner.
  - Built the custom image, creating a csye6225 user, installing dependencies, copying artifacts and configuration files, and ensuring ownership by csye6225.
  - User systemd to add and enable a systemd service file for my application to start on instance launch.

### Infrastructure as Code with Terraform ###
- **Firewall**:Setting up firewall rules to allow traffic to the application's port `8080` while blocking SSH access from the internet.
- **Compute Engine Instance**: Use custom image to build an instance, with balanced boot disk type and 100G size.
