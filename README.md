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
- `SSH_PASSWORD`: SSH password for the target server (if using password authentication)
- `SSH_PRIVATE_KEY`: Private SSH key (if using key-based authentication)
- `SSH_PASSPHRASE`: Passphrase for SSH key (if applicable)
- `SENDGRID_API_KEY`: SendGrid API key for email services

### Container Configuration

The deployment creates the following Docker containers:

- **cleonora-db**: PostgreSQL database (port 5440:5432)
- **cleonora-rest**: REST API service (port 7939:8082)
- **cleonora-admin**: Admin frontend (port 7940:8081)

All containers are connected via the `cleonora-network` Docker network.

## Server Configuration Requirements

### SSH Authentication

The workflow supports two authentication methods:

#### 1. Password Authentication
- Set the `SSH_PASSWORD` secret in GitHub settings
- Ensure password authentication is enabled in your SSH server config (`/etc/ssh/sshd_config`)

#### 2. SSH Key Authentication (More Secure)
1. Generate an SSH key pair if you don't have one:
   ```bash
   ssh-keygen -t ed25519 -C "github-actions"
   ```
2. Add the public key to your server's `~/.ssh/authorized_keys`
3. In GitHub repository settings, add the private key as `SSH_PRIVATE_KEY` secret
4. If your key has a passphrase, add it as `SSH_PASSPHRASE` secret
5. Edit the workflow file to uncomment the key authentication lines and comment out the password line

### Firewall Settings

To ensure proper connectivity for GitHub Actions, make sure your server allows incoming connections on the following ports:

- **Port 22**: SSH access (required for deployment)
- **Port 5440**: PostgreSQL database
- **Port 7939**: CleonoraRest API
- **Port 7940**: CleonoraAdmin frontend

For Ubuntu/Debian servers, you can use:

```bash
sudo ufw allow 22/tcp
sudo ufw allow 5440/tcp
sudo ufw allow 7939/tcp
sudo ufw allow 7940/tcp
sudo ufw reload
```

### Network Troubleshooting

If you encounter SSH timeout errors ("dial tcp: i/o timeout"), check:

1. Verify your server's IP address is correct in the GitHub secrets
2. Ensure SSH service is running: `sudo systemctl status sshd`
3. Check if port 22 is open: `nc -vz your_server_ip 22`
4. Make sure your firewall allows incoming SSH connections
5. Verify network ACLs if using a cloud provider (AWS Security Groups, etc.)
6. GitHub runners must be able to access your server over the public internet

### Debugging Connection Issues

If you're still experiencing connection problems:

1. Try to SSH from your local machine to verify server accessibility:
   ```bash
   ssh username@your-server-ip
   ```

2. Check SSH server logs for connection attempts:
   ```bash
   sudo journalctl -u ssh
   ```

3. Temporarily enable verbose SSH logging by editing `/etc/ssh/sshd_config`:
   ```
   LogLevel DEBUG3
   ```
   Then restart the SSH service:
   ```bash
   sudo systemctl restart sshd
   ```

4. Consider using an SSH connection timeout script in the workflow to test connectivity before attempting deployment:
   ```bash
   timeout 10s nc -z your-server-ip 22
   ``` 