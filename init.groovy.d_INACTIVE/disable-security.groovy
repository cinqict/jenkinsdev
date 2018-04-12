#!/usr/bin/env groovy
import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

instance.disableSecurity()
instance.save()