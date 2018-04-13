#!/usr/bin/env groovy

Jenkins jenkins = Jenkins.get()
jenkins.disableSecurity()
jenkins.save()