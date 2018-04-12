
#### ToDo
- Title
- Spelling checker
- Update list of plugins
- Add example branch to github
- Use `jenkins.instance` or `jenkins.getInstance()`
- Resolve all LINKS
- advanced or not
- Are the import jenkins* and hudson* really needed?


# Develop Jenkinsfile and init Groovy files

## Develop Init Groovy Code  (second blog?)
As said before Jenkins offers a hook, the init.groovy.d directory, where you can add your Groovy code for all initial jenkins configurations.
In general, these init Groovy code is not very well documented.  
The strategy that works best for me is first search for examples which come close to what you want and from there try to look into the source code of Jenkins or the plugin that you use.
For instance the above code was quit easily created after looking in the `hudson.security.*` code.
<LINK?>

To save some restarts, you can test your init Groovy code by pasting it in the Jenkins GUI.

Manage Jenkins > Script Console

<Link or Small Picture?>


## Develop Jenkinsfiles  (second blog?)
The documentation for Jenkinsfile as improved massively the last months and is the right place to start developing your Jenkinsfile.
<LINK pipeline syntax>
For testing you Jenkinsfile I use again to strategies. 
When trying simple examples from the documentation it is easier to just create a pipeline job in the Jenkins GUI.
There you get basic Groovy editor where you can paste you Jenkinsfile in, run it and edit again. 
This is the fasted way to test your code, but is only suitable for small peaces.
Developing your Jenkinsfile in a IDE is much nicer. 
Assuming you are creating a Jenkinsfile in the repository of your application, you can just commit and push you changes and then run the Jenkins job which you have defined as a multibranch project.
This is the real process.
The downside is that testing a small changes requires a commit. 
Since I do not want to have ten commits for a single feature, I use for every commit:

```bash
git reset --soft HEAD~1
git commit -am "add feature 1"
git push --force
```

### Intelij autocompletion and documentation
Jenkins offers autocompletion and documentation for the pipeline syntax in your Intelij project. 
More precise, for all object inside the stage blocks.
Steps:

- Download `pipeline.gdsl` from Jenkins:
  - Go to a pipeline > Pipeline Syntax > IntelliJ IDEA GDSL
- Put `pipeline.gdsl` in the classpath of your project
  - Create dir `lib`
  - Copy `pipeline.gdsl`
  - Right click on `lib` > Mark Directory As > Resources Root
  
References: 
- https://st-g.de/2016/08/jenkins-pipeline-autocompletion-in-intellij
- https://gist.github.com/arehmandev/736daba40a3e1ef1fbe939c6674d7da8


## List of references
Look in the blog for LINK !!!



## Git on intranet (second blog?)
When the remote Git server run on an intranet, you need to specify your intranet DNS server.

```bash
# Get DNS server IP
DNS_SRV_IP=$(nmcli dev show | grep DNS | awk '{ print $2 ; exit }' )
# start container
docker run -p 80:8080 --dns=${DNS_SRV_IP} jenkinsdev
```


## Configure Docker in Jenkins (second blog?)

Add plugin to list

```bash
  docker-plugin:latest
```

The docker-plugin requires some extra configuration.

```dockerfile
# Enable Docker client
# Start image with argument: "-v /var/run/docker.sock:/var/run/docker.sock"
USER root
RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
  && tar xzvf docker-17.04.0-ce.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-17.04.0-ce.tgz \
  # Note: 999 is the group_id of my local docker group
  && groupadd -g 999 docker \
  # Add jenkins user to the docker group
  && usermod -aG docker jenkins
USER jenkins
```



Eric Cornet <br>
CI/CD Engineer 


