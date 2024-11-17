####  scmPipeline Pipeline base on SCM 
scmPipeline using **ci.infra.pipeline.ScmBuild**  as parameter properties ：

prop | required | mark                                               |
-- |----------|----------------------------------------------------|
buildEnv | Yes      | Jenkins node server name，options（test,deploy [^1]) |
repository | Yes      | repository ，support Git currently                  |
repositoryCredentialsId | No       | credentials id for repository in Jenkins           |
repositoryBranch | Yes      | branch name of repository to checkout              |
buildName | No       | build name, like env.JOB_NAME                      |
buildImage | Yes      | docker image to build （include hostname）           |
buildDescription | No       | description information with this build            |
skipDeploy | No | if skip to deploy, default no                      
remoteServerName | No | remote server info in Jenkins to deploy            
k8sNamespace | Yes      | namespace of app in k8s                            |
k8sDeployment | Yes      | deployment name of app in k8s                      |
k8sContainer | Yes      | container name of app in k8s                       | 


[^1]: test test env，deploy prod env

#### imageTagPipeline
### Use case
-  add repository to jenkins share library as ci_pipeline_lib with version master
-  build with source code from repository

```groovy
import ci.infra.pipeline.ScmBuild

@Library("ci_pipeline_lib@master") _
def buildInfo = new ci.infra.pipeline.ScmBuild()
buildInfo.setBuildEnv("test")
buildInfo.setRepository("https://gitlab.com/app1.git")
buildInfo.setRepositoryBranch("develop")
buildInfo.setBuildTool("maven")
buildInfo.setBuildImage("goharbor.com/build/app1")
buildInfo.setDockerfile("DockerFile")
buildInfo.setK8sNamespace("apps")
buildInfo.setSkipDeploy(true) //skip to deploy,which means setting k8s info useless
buildInfo.setK8sDeployment("app1")
buildInfo.setK8sContainer("app1")
scmPipeline(buildInfo)
```
- build with docker image which exists in registry

```groovy
import ci.infra.pipeline.ImageBuild

@Library("ci_pipeline_lib@master") _
def buildInfo = new ci.infra.pipeline.ImageBuild()
buildInfo.setBuildEnv("deploy")
buildInfo.setRepository("https://gitlab.com/app1.git")
buildInfo.setBuildImage("build/app1")
buildInfo.setReleaseImage("goharbor.com/release/app1")
buildInfo.setK8sNamespace("apps")
buildInfo.setSkipDeploy(true)
buildInfo.setTagSource(true)
buildInfo.setK8sDeployment("app1")
buildInfo.setK8sContainer("app1")
imageTagPipeline(buildInfo)
```