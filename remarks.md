# Remarks

## Initialise Jenkins instance in init Groovy

Variation on internet:
- def instance = Jenkins.getInstance()
- Jenkins jenkins = Jenkins.instance
- Jenkins jenkins = Jenkins.getInstance()

Jenkins source code: http://javadoc.jenkins-ci.org/jenkins/model/Jenkins.html

getInstance()
Deprecated. 
This is a historical alias for getInstanceOrNull() but with ambiguous nullability. Use get() in typical cases.

- Jenkins.get() from 2.98

In the docker-plugin they use:

```groovy
// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()
```

ref: https://plugins.jenkins.io/docker-plugin

### Conclusion
Non of them seems to be preferred. 
Since I consider docker-plugin developers to be most capable, 
I will go for that usage.
Which from 2.98 becomes:

```groovy
// get Jenkins instance
Jenkins jenkins = Jenkins.get()
```



## ToDo blog1
- Spelling checker  ---> DONE
- Update list of plugins  ---> DONE
- Use `jenkins.instance` or `jenkins.getInstance()`   ---> DONE, see remarks.md
- advanced or not  ---> DONE, second blog
- Are the import jenkins* and hudson* really needed? ---> DONE
- Resolve all LINKS ---> DONE
- Add example branch to github ---> DONE


