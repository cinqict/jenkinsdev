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

class MultiBranchProject{
    String gitRepoUrl;
    String repoName;
    String jobName;
    String jenkinsfileName;
    Jenkins jenkins;

    public MultiBranchProject (gitRepoUrl, jenkinsfileName = "Jenkinsfile") {
        this.gitRepoUrl = gitRepoUrl;
        this.repoName = gitRepoUrl.tokenize(".")[-2].tokenize("/")[-1];
        this.jenkinsfileName = jenkinsfileName;
        // Job name based on repo and Jenkinsfile
        this.jobName = this.repoName + "_" + this.jenkinsfileName;
        // Define Jenkins
        this.jenkins = Jenkins.instance;
    }

    public void create() {
        // Create MultiBranch pipeline
        WorkflowMultiBranchProject mbp = this.jenkins.createProject(WorkflowMultiBranchProject.class, this.jobName)

        // Configure the Jenkinsfile this MBP uses
        mbp.getProjectFactory().setScriptPath(this.jenkinsfileName)

        // Define Git repo
        //GitSCMSource(id, gitRepo, credentialsId, includes, excludes, ignoreOnPushNotifications)
        GitSCMSource gitSCMSource = new GitSCMSource(null, this.gitRepoUrl, "", "*", "", false)
        BranchSource branchSource = new BranchSource(gitSCMSource)

        // Add Git repo as source to MBP
        PersistedList sources = mbp.getSourcesList()
        sources.add(branchSource)

        // Save config
        this.jenkins.save()
    }

    public void build() {
        // Trigger initial build (scan)
        this.jenkins.getItem(this.jobName).scheduleBuild()
    }
}


/* MAIN */
MultiBranchProject project = new MultiBranchProject("https://github.com/Dirc/jenkinsdev.git","Jenkinsfile")
project.create()
project.build()


MultiBranchProject project = new MultiBranchProject("http://code.able.nv/scm/~ec1090/multibanch-examples.git","Jenkinsfile_soapui")
project.create()
project.build()


