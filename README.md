## Repository jx_common_pl (Jenkins common pipeline library)
### 流水线说明
####  scmPipeline 基于源代码进行构建的流水线
scmPipeline 使用 ci.infra.pipeline.ScmBuild 作为构建参数，主要属性如下：

属性 | 是否必须 | 说明                         |
-- |------|----------------------------|
buildEnv | 是    | 构建节点，可选择（test,deploy [^1]) |
repository | 是    | 源代码仓库地址，目前只支持gitlab        |
repositoryCredentialsId | 否 | 源代码使用账户Id                  |
repositoryBranch | 是 | 签出代码的分支名称                  |
buildName | 否 | 构建名称，等同于env.JOB_NAME       |
buildImage | 是 | 构建的镜像全路径（包括主机地址）           |
buildDescription | 否 | 构建描述信息                     |
k8sNamespace | 是 | 需要发布的部署所在的命名空间             |
k8sDeployment | 是 | 发布的部署名                     |
k8sContainer | 是 | 发布的部署的容器名                  | 


[^1]: test 对应的是测试环境的部署，deploy对应的是生成环境的部署

#### imageTagPipeline
### Use case
-  add repository to jenkins share library as mindforce-ci-lib with version master
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
buildInfo.setSkipDeploy(true)
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