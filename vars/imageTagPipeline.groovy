import ci.infra.pipeline.ImageBuild

def call(ImageBuild imageBuild){
    def globalBuildInfo = globalVars("config/global.json")
    def repository = imageBuild.getRepository()
    def buildImages = imageBuild.getBuildImage()
    def releaseImage = imageBuild.getReleaseImage()
    pipeline {
        agent {
            node {
                label imageBuild.getBuildEnv()
            }
        }
        parameters {
            imageTag(
                    name: 'DOCKER_IMAGE',
                    description: '请选择待发布的版本',
                    image: buildImages,
                    filter: '.*',
                    defaultTag: '',
                    registry: globalBuildInfo.'docker.registry',
                    credentialId: globalBuildInfo.'docker.registry.credentials',
                    tagOrder: 'DSC_VERSION'
            )
            string(
                    name: 'RELEASE_TAG',
                    defaultValue: '',
                    description: '请输入生产环境对应的版本号'
            )
        }

        stages {
            stage('check user choice') {
                steps {
                    script{
                        if(!imageBuild.checkValidation()){
                            imageBuild.getCheckErrorInfo().each { println(it)}
                            error("check build info failed!")
                        }
                        if(DOCKER_IMAGE == "") {
                            error("please select image from list")
                        }
                        if(RELEASE_TAG == "") {
                            error("please input release tag version ")
                        }
                    }
                    echo "build node : ${NODE_NAME}"
                    echo "jenkins workspace:  ${WORKSPACE}"
                    echo "DOCKER_IMAGE : ${DOCKER_IMAGE}"
                    echo "RELEASE_TAG  : ${RELEASE_TAG}"
                }
            }
            stage ("tagging release image") {
                steps{
                    script {
                        def buildImageTag = "goharbor.ebsig.com/${DOCKER_IMAGE}"
                        env.releaseImageTag = "${releaseImage}:${RELEASE_TAG}"
                        echo "build image -----> ${buildImageTag}"
                        echo "release image  --> ${releaseImageTag}"
                        def dockerLabel = buildTool.tagDockerImage(globalBuildInfo.'docker.server',globalBuildInfo.'docker.registry',globalBuildInfo.'docker.registry.credentials',"docker",buildImageTag,releaseImageTag)
                        def buildMap = parseDockerLabel(dockerLabel)
                        def commitId = buildMap.commitId
                        def buildId = buildMap.buildId
                        if(imageBuild.getTagSource()) {
                            if (commitId != "" && buildId != "") {
                                println("tagging source code to git")
                                def tagName = "release-${RELEASE_TAG}"
                                def comment = "new tag from jenkins pipeline [" + env.JOB_NAME + " ],build id " + buildId +" docker images ${releaseImageTag}"
                                scmTool.tagSourceToGit(repository, globalBuildInfo.'repository.credentials', commitId, tagName, comment)
                            }else{
                                println("try to tag source code but commit id or build id missing in docker image")
                            }
                        }
                    }
                }
            }
            stage ("release to production environment") {
                when {
                    expression {
                        return !imageBuild.getSkipDeploy()
                    }
                }
                steps {
                    script {
                        def namespace = imageBuild.getK8sNamespace()
                        def deployment = imageBuild.getK8sDeployment()
                        def container = imageBuild.getK8sContainer()
                        def remoteServer = imageBuild.getRemoteServerName()
                        if(remoteServer == null || remoteServer.isEmpty()) {
                            deployTool.applyToK8s(namespace,deployment,container,releaseImageTag)
                        }else{
                            def cmd = deployTool.applyToK8s(namespace,deployment,container,releaseImageTag,true)
                            deployTool.applyBySSH(remoteServer,cmd)
                        }
                    }
                }
            }
        }
    }
}