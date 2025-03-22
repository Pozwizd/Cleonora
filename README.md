# Cleonora Project

This repository contains two main projects:
- **CleonoraRest**: Backend REST API service
- **CleonoraAdmin**: Admin frontend application

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment. Both applications are automatically built, containerized, and deployed to the server when changes are pushed to the main branch.

### Workflow Overview

The CI/CD pipeline consists of the following jobs:

1. **Build and Deploy CleonoraRest**
   - Builds the REST API application using Gradle
   - Creates a Docker image and pushes it to Docker Hub

2. **Build and Deploy CleonoraAdmin**
   - Builds the Admin frontend application using Gradle
   - Creates a Docker image and pushes it to Docker Hub

3. **Deploy to Server**
   - Ensures the PostgreSQL database is running
   - Deploys the latest version of both services
   - Sets up the necessary network and configuration

### Required Secrets

The following secrets must be configured in GitHub repository settings:

- `DOCKER_USERNAME`: Docker Hub username
- `DOCKER_PASSWORD`: Docker Hub password
- `SSH_HOST`: Target deployment server hostname or IP
- `SSH_USERNAME`: SSH username for the target server
- `SSH_PASSWORD`: SSH password for the target server
- `SENDGRID_API_KEY`: SendGrid API key for email services

### Container Configuration

The deployment creates the following Docker containers:

- **cleonora-db**: PostgreSQL database (port 5440:5432)
- **cleonora-rest**: REST API service (port 7939:8082)
- **cleonora-admin**: Admin frontend (port 7940:8081)

All containers are connected via the `cleonora-network` Docker network. 