import ci.infra.pipeline.ScmBuild

def call(ScmBuild scmBuild) {
    def globalBuildInfo = globalVars("config/global.json")
    def repository = scmBuild.getRepository()
    def branch = scmBuild.getRepositoryBranch()
    pipeline {
        agent {
            node {
                label scmBuild.getBuildEnv()
            }
        }
        stages {
            stage('check config variables') {
                steps {
                    script{
                        if(!scmBuild.checkValidation()){
                            scmBuild.getCheckErrorInfo().each { println(it)}
                            error("check build info failed!")
                        }
                    }
                    println("check JENKINS Environment variables ")
                    echo "build node : ${NODE_NAME} workspace:  ${WORKSPACE} tag:  ${BUILD_TAG} "
                    println("check system configuration variables ")
                    echo "docker server : ${globalBuildInfo.'docker.server'} respoistory credentials id: ${globalBuildInfo.'repository.credentials'}"
                    println("check build variables ")
                    echo "build repository ${repository} branch ${branch}"
                }
            }
            stage('checkout from repository'){
                steps {
                    script {
                        def commitId = scmTool.checkoutSourceFromGit(repository,branch,globalBuildInfo.'repository.credentials')
                        echo "repository latest commit id ${commitId} "
                        env.appCommitId = commitId
                    }
                }
            }
            stage("build code with tool") {
                steps {
                    script {
                        def toolName = scmBuild.getToolName()
                        switch (scmBuild.getBuildTool()) {
                            case "java":
                                println("java build using maven by default ...")
                                if(toolName == null || toolName.isEmpty()) {
                                    toolName = "jdk-1.8"
                                }
                                buildTool.buildCodeWithMaven("maven",toolName,scmBuild.getBuildWithTestSkip())
                                break
                            case "maven":
                                if(toolName == null || toolName.isEmpty()) {
                                    toolName = "jdk-1.8"
                                }
                                buildTool.buildCodeWithMaven("maven",toolName,scmBuild.getBuildWithTestSkip())
                                break
                            case "node":
                                if(toolName == null || toolName.isEmpty()) {
                                    toolName = "node18"
                                }
                                if(toolName == "node20") {
                                    println("node 18+ build in docker  env")
                                    buildTool.buildCodeWithNodeInDocker("node20","docker",scmBuild.getBuildPath())
                                }else{
                                    println("node build in node env")
                                    buildTool.buildCodeWithNode(toolName,scmBuild.getBuildPath())
                                }
                                break
                            case "golang":
                                if(toolName == null || toolName.isEmpty()) {
                                    toolName = "go17"
                                }
                                buildTool.buildCodeWithGolang(toolName,scmBuild.getBuildPath())
                                break
                            default :
                                println("build tool is not defined to tool " + scmBuild.getBuildTool() + ",using source package (source code will not be compiled!!!!)")
                        }
                    }
                }
            }
            stage('build docker image ') {
                steps {
                    script {
                        if(globalBuildInfo.'docker.build_kit' == "enabled"){
                            env. DOCKER_BUILDKIT = "1"
                        }
                        def imageBuild = scmBuild.getBuildImage()
                        def imageTag = "${imageBuild}:build-${env.BUILD_ID}"
                        buildTool.buildDockerImage(globalBuildInfo.'docker.server',globalBuildInfo.'docker.registry',globalBuildInfo.'docker.registry.credentials',imageTag,env.appCommitId,"docker",scmBuild.getDockerfile())
                    }
                }
            }
            stage('deploy image to environment runtime') {
                when {
                    expression {
                        return !scmBuild.getSkipDeploy()
                    }
                }
                steps {
                    script {
                        def imageBuild = scmBuild.getBuildImage()
                        def imageTag = "${imageBuild}:build-${env.BUILD_ID}"
                        def namespace = scmBuild.getK8sNamespace()
                        def deployment = scmBuild.getK8sDeployment()
                        def container = scmBuild.getK8sContainer()
                        def remoteServer = scmBuild.getRemoteServerName()
                        if(remoteServer == null || remoteServer.isEmpty()) {
                            deployTool.applyToK8s(namespace,deployment,container,imageTag)
                        }else{
                            def cmd = deployTool.applyToK8s(namespace,deployment,container,imageTag,true)
                            deployTool.applyBySSH(remoteServer,cmd)
                        }
                    }
                }
            }
        }
    }
}