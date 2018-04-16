#!/usr/bin/env groovy
import jenkins.model.*

Jenkins jenkins = Jenkins.get()
jenkins.disableSecurity()
jenkins.save()