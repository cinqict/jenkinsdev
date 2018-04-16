
# Jenkins Development Image
While developing my Jenkinsfile and init Groovy scripts, 
I needed a Jenkins Docker image that I could launch and destroy easily with a minimal amount of manual interaction. 
To my surprise, I needed to google multiple references to set this up. 
Enough reason for me to share my findings and results.

Jenkins offers a official Docker image [jenkins/jenkins](https://hub.docker.com/r/jenkins/jenkins/). 
This is a good starting point to create your own image since it contains an extensive readme
with al kinds of code snippets that you can use in your Dockerfile. 

```bash
# Get latest image
docker pull jenkins/jenkins
# Run Jenkins on port 80
docker run -p 80:8080 jenkins/jenkins
```

Browse to `http://localhost:80` you immediately see some manual steps which are nice for a first Jenkins experience, but not in your development environment. 
You need to:
- Copy a secret key from the log file, 
- Select plugins
- Login with a default username/password 
- Create a project

So this gives us four manual steps we want to get rid of. 
To do this, we will create a Dockerfile and some initial Groovy script to configure Jenkins during start up.

## Setting things up
We start our Dockerfile by extending the Jenkins image as described in the readme ot the `jenkins/jenkins` image.

```dockerfile
# Extended from https://github.com/jenkinsci/docker/blob/master/README.md
FROM jenkins/jenkins
```

After each step, we will need to build and run our new image so we can verify the result.

```bash
# Build
docker build -t jenkinsdev .
# Run 
docker run -p 80:8080 jenkinsdev
```


## Disable Setup Wizard
The first step to take is to disable the welcome page called "SetupWizard" where you need to add a secret key. 
This can be done by adding a Java option to your Dockerfile.

```dockerfile
# Skip setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
```

## Remove Credentials
As a second step we do not want to login upon each restart, so we need to remove the required default credentials. 
This is done with a small Groovy script which can be added to the `init.groovy.d` directory. All Groovy files in this directory will be executed during startup.

Created the file: `init.groovy.d/disable-securit.groovy`

```groovy
#!/usr/bin/env groovy
import jenkins.model.*

Jenkins jenkins = Jenkins.get()
jenkins.disableSecurity()
jenkins.save()
```

And put the whole `init.groovy.d` directory in your image using COPY in the Dockerfile:

```dockerfile
# Add groovy script to Jenkins hook
COPY --chown=jenkins:jenkins init.groovy.d/ /var/jenkins_home/init.groovy.d/
```

The `--chown=jenkins:jenkins` option is added here to resolve permission problems. 


## Add Plugins
Plugins can be installed easily by giving the plugin ID to the `install-plugins.sh` script. 
This script is provided in the official Jenkins image. 
I have listed some useful plugins here which we add to our Dockerfile. 

```dockerfile
# Get plugins
RUN /usr/local/bin/install-plugins.sh \
  workflow-multibranch:latest \
  pipeline-model-definition:latest \
  pipeline-stage-view:latest \
  git:latest
```


## Add Multibranch Pipeline
During startup I want Jenkins to load my Git repository containing the Jenkinsfile.
This can be done by adding another Groovy script `initial_project.groovy` to the `init.groovy.d` directory. 
I have added a Hello World Jenkinsfile example to the repository so we let Jenkins search in https://github.com/cinqict/jenkinsdev.

```groovy
#!/usr/bin/env groovy
import jenkins.model.*
import hudson.util.PersistedList
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*

// Git repo url containing a Jenkinsfile
String gitRepoUrl = "https://github.com/cinqict/jenkinsdev"

// Job name based on repository name
String jobName = gitRepoUrl.tokenize(".")[-2].tokenize("/")[-1]


// Create MultiBranch pipeline
Jenkins jenkins = Jenkins.get()
WorkflowMultiBranchProject mbp = jenkins.createProject(WorkflowMultiBranchProject.class, jobName)

// Define Git repo
//GitSCMSource(id, gitRepo, credentialsId, includes, excludes, ignoreOnPushNotifications)
GitSCMSource gitSCMSource = new GitSCMSource("not_null", gitRepoUrl, "", "*", "", false)
BranchSource branchSource = new BranchSource(gitSCMSource)

// Add Git repo as source to MBP
PersistedList sources = mbp.getSourcesList()
sources.add(branchSource)

// Trigger initial build (scan)
jenkins.getItem(jobName).scheduleBuild()

// Save config
jenkins.save()
```

Note: We have already copied the `init.groovy.d` directory in our Dockerfile. 
So when you rebuild and start the image, Jenkins will start with my new project.


## Dockerfile
Both the list of plugins and the init.groovy.d scripts will change from time to time.
Following the [Docker best practices](https://cinqict.github.io/post/christiaan/docker_file_best_practices/) , we should put them at the end of my Dockerfile.
Since downloading the plugins takes the biggest amount of time, we only want to do this if it's really necessary.
So it is best to put the plugins above the init.groovy.d scripts. 

We end up with the following Dockerfile:

```dockerfile
# Extended from https://github.com/jenkinsci/docker/blob/master/README.md
FROM jenkins/jenkins:lts

# Skip setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Get plugins
RUN /usr/local/bin/install-plugins.sh \
  workflow-multibranch:latest \
  pipeline-model-definition:latest \
  pipeline-stage-view:latest \
  git:latest \
  credentials:latest

# Add groovy script to Jenkins hook
COPY --chown=jenkins:jenkins init.groovy.d/ /var/jenkins_home/init.groovy.d/

# Remark: there is no CMD or statement. Since jenkins/jenkins:lts image uses an ENTRYPOINT, this image will inherit that behavior.
```

## Summarize
These Dockerfile and init Groovy scripts gives a simple Jenkins image which you can run en destroy in seconds and is therefore very useful for developing Jenkinsfiles and init Groovy scripts.


Eric Cornet <br>
CI/CD Engineer 

All code can be found on https://github.com/cinqict/jenkinsdev

