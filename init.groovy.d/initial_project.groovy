#!/usr/bin/env groovy
import hudson.util.PersistedList
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*

// Git repo url containing a Jenkinsfile
String gitRepoUrl = "https://github.com/Dirc/jenkinsdev.git"

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