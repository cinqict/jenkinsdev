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

- Jenkins.get() from 2.98 ???

In the docker-plugin they use:

```groovy
// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()
```

Non of them seems to be preferred. 
Since I consider docker-plugin developers to be most capable, 
I will go for that usage.
Which from 2.98 becomes:

```groovy
// get Jenkins instance
Jenkins jenkins = Jenkins.get()
```


