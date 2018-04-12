#!/usr/bin/env groovy
import jenkins.model.*
import hudson.security.*

Jenkins jenkins = Jenkins.get()
jenkins.disableSecurity()
jenkins.save()