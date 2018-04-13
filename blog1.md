
# Jenkins Development Image
For developing my Jenkinsfile and init Groovy scripts, 
I needed a Jenkins Docker image that I could launch and destroy easily with the least amount of manual interaction. 
To my surprise, I needed to google for multiple references to set this up. 
Enough reason to share my findings and result.

Jenkins offers a official Docker image `jenkinsci`. 
This is a good starting point to create your own image since there is a extensive readme as well, 
with al kinds of code snippets for your Dockerfile. 

Running the jenkinsci image you see immediately some manual steps which are nice for a first Jenkins experience, but not for your development environment. 
You need to
- Add a secret key from the log file, 
- Select plugins, 
- Login with a default username/password 
- Add your project

This gives us four manual steps which we want to get rid of. 
To achieve this, we will create a Dockerfile and some initial Groovy script to configure Jenkins during start up.

## Set things up
We start our Dockerfile by extending the Jenkins image as described in the jenkinsci readme.

```dockerfile
# Based on https://github.com/jenkinsci/docker/blob/master/README.md
FROM jenkins/jenkins:lts
```

After each step, we will need to build and run our new image so we can verify the result.

```bash
# Build
docker build -t jenkinsdev .
# Run 
docker run -p 80:8080 jenkinsdev
```


## Disable Setup Wizard
The first step we take is to disable the welcome page called "SetupWizard" where you need to add a secret key. 
This can be done by adding a Java option to your Dockerfile.

```dockerfile
# Skip setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
```

## Remove Credentials
Second we do not want to login each restart, hence we need to remove the required default credentials. 
This can be done with a small Groovy script which we add to the init.groovy.d directory. All Groovy files in there will be executed during startup.

We create the file: `init.groovy.d/disable-securit.groovy`

```groovy
#!/usr/bin/env groovy

Jenkins jenkins = Jenkins.get()
jenkins.disableSecurity()
jenkins.save()
```

And we put the whole `init.groovy.d` directory in our image using COPY in the Dockerfile:

```dockerfile
# Add groovy script to Jenkins hook
COPY --chown=jenkins:jenkins init.groovy.d/ /var/jenkins_home/init.groovy.d/
```

The `--chown=jenkins:jenkins` option is added here to resolve permission problems. 


## Add Plugins
Plugins can be installed easily by giving the plugin ID to the `install-plugins.sh` script. 
This script is provided in the official Jenkins image on which we build. 
I have listed some useful plugins here which we can add to our Dockerfile. 

```dockerfile
# Get plugins
RUN /usr/local/bin/install-plugins.sh \
  workflow-multibranch:latest \
  pipeline-model-definition:latest \
  pipeline-stage-view:latest \
  git:latest \
  credentials:latest 
```


## Add Multibranch Pipeline
During startup we want Jenkins to load our Git repository containing the Jenkinsfile.
This can be done by adding another Groovy script `initial_project.groovy` to the `init.groovy.d` directory. 
I have added a Hello World Jenkinsfile example to the repository so we let Jenkins look in https://github.com/Dirc/jenkinsdev.git.

```groovy
#!/usr/bin/env groovy
import hudson.util.PersistedList
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*

// Git repo url containing a Jenkinsfile
String gitRepoUrl = "https://github.com/Dirc/jenkinsdev.git"

// Job name based on repository name
String jobName = gitRepoUrl.tokenize(".")[-2].tokenize("/")[-1]


// Define Jenkins
//Jenkins jenkins = Jenkins.instance
Jenkins jenkins = Jenkins.get()

// Create MultiBranch pipeline
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

Note: We already COPY the `init.groovy.d` directory in our Dockerfile. 
So when we rebuild and start the image, Jenkins will start with our new project.


## Dockerfile
Both the list of plugins and the init.groovy.d scripts will change from time to time.
Following the [Docker best practices](https://cinqict.github.io/post/christiaan/docker_file_best_practices/) , we should put them at the end of our Dockerfile.
Since downloading plugins takes most time, we only want to do it if really necessary, hence we put the plugins above the init.groovy.d scripts. 

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
This Dockerfile and init Groovy scripts gives use a simple Jenkins image which we can run en destroy in seconds. 
This is very useful for developing your Jenkinsfiles and init Groovy scripts.


Eric Cornet <br>
CI/CD Engineer 

All code can be found on https://github.com/Dirc/jenkinsdev

