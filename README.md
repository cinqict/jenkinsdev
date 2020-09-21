# Blog: jenkinsdev

This repository contains all needed code which is used in the blog on [Jenkins Development Image](https://cinqict.nl/building-a-jenkins-development-docker-image/).

## jenkinsdev

Jenkinsdev is ment for developing your Jenkinsfile and Groovy scripts. 
My intention was to create a Jenkins server which I can destroy and spin up again in seconds, with the least amount of manual work.

### Build and Run

Build image

```bash
docker pull jenkins/jenkins
docker build -t jenkinsdev .
```

Run Jenkins

```bash
docker run -p 8080:8080 jenkinsdev
```

### Files explained

#### init.groovy.d
All files in init.groovy.d are executed during startup (Jenkins hook).

- disable-security.groovy: disable need for manual login
- initial_project.groovy: import git project.

#### Dockerfile
Extended from https://github.com/jenkinsci/docker/blob/master/README.md

More remarks inside the Dockerfile.

#### Jenkinsfile Example
Example of a Hello World Jenkinsfile. 
Note that the Jenkinsfile normally will be in your application repository.
