# Extended from https://github.com/jenkinsci/docker/blob/master/README.md
FROM jenkins/jenkins

# Skip setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Get plugins
RUN /usr/local/bin/install-plugins.sh \
  workflow-multibranch:latest \
  pipeline-model-definition:latest \
  pipeline-stage-view:latest \
  git:latest 

# Add groovy script to Jenkins hook
COPY --chown=jenkins:jenkins init.groovy.d/ /var/jenkins_home/init.groovy.d/

# Remark: there is no CMD or statement. Since jenkins/jenkins:lts image uses an ENTRYPOINT, this image will inherit that behavior.