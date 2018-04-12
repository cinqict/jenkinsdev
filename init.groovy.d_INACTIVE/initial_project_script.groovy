#!/usr/bin/env groovy
import jenkins.*
import jenkins.model.*
import jenkins.model.Jenkins
import hudson.*
import hudson.model.*
import hudson.util.PersistedList
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*

// Remark: Not tested for private repo's or for SSH

def addMultiBranchProject(String gitRepoUrl, String jenkinsfileName = "Jenkinsfile", boolean scanBuild = false) {
    // Project variables
    String repoName = gitRepoUrl.tokenize(".")[-2].tokenize("/")[-1];
    // Job name based on repo and Jenkinsfile
    String jobName = repoName + "_" + jenkinsfileName;

    // Define Jenkins
    Jenkins jenkins = Jenkins.instance

    // Create MultiBranch pipeline
    WorkflowMultiBranchProject mbp = jenkins.createProject(WorkflowMultiBranchProject.class, jobName)

    // Configure the Jenkinsfile this MBP uses
    mbp.getProjectFactory().setScriptPath(jenkinsfileName)

    // Define Git repo
    //GitSCMSource(id, gitRepo, credentialsId, includes, excludes, ignoreOnPushNotifications)
    GitSCMSource gitSCMSource = new GitSCMSource(null, gitRepoUrl, "", "*", "", false)
    BranchSource branchSource = new BranchSource(gitSCMSource)

    // Add Git repo as source to MBP
    PersistedList sources = mbp.getSourcesList()
    sources.add(branchSource)

    // Trigger initial build (scan)
    if (scanBuild) {
        jenkins.getItem(jobName).scheduleBuild()
    }
    // Save config
    jenkins.save()
}


addMultiBranchProject("https://github.com/Dirc/jenkinsdev.git", "Jenkinsfile", true)
