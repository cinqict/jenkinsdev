# jenkinsdev
Jenkins is ment for developing your Jenkinsfile and Groovy scripts. 
My intention was to create a Jenkins server which I can destroy and spin up again in seconds, with the least amount of manual work.

## Build and Run
Based on https://github.com/jenkinsci/docker/blob/master/README.md

Build image

```bash
docker build -t jenkinsdev .
```

Run Jenkins

```bash
docker run -p 8080:8080 jenkinsdev
```

## Files explained

### init.groovy.d
All files in init.groovy.d are executed during startup (Jenkins hook).

- disable-security.groovy: disable need for manual login
- initial_project.groovy: import git project. based on https://github.com/peterjenkins1/jenkins-scripts/blob/master/add-multibranch-pipeline-job.groovy

### Dockerfile
Extended from https://github.com/jenkinsci/docker/blob/master/README.md
More remarks inside the Dockerfile.

### Jenkinsfile Examples
Examples of Hello World Jenkinsfile. 
Note that the Jenkinsfile normally will be in your appication repository.
