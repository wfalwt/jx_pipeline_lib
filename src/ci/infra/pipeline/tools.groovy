package ci.infra.pipeline

def checkoutSourceFromGit(branchName){
    def appSCM = checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branchName}"]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [],
            submoduleCfg: [],
            userRemoteConfigs:[[credentialsId: repositoryCredentialsId, url: repository]]
    ])
    echo "commit id : ${appSCM.GIT_COMMIT}"
    env.appCommit = appSCM.GIT_COMMIT
}

def buildCodeWithMaven(skipTest=true) {
    def mavenPath = tool 'maven'
    println(mavenPath)
    def jdkPath = tool 'jdk-1.8'
    println(jdkPath)
    env.PATH="${mavenPath}/bin:${jdkPath}/bin:${env.PATH}"
    sh "mvn clean"
    sh "SPRING_PROFILES_ACTIVE=test mvn package -Dmaven.test.skip=${skipTest}"
}


def buildCodeWithNode(nodeName="node18") {
    def nodePath = tool name: nodeName, type: 'nodejs'
    env.PATH="${nodePath}/bin:${env.PATH}"
    sh """
        npm config set strict-ssl false
        npm --registry https://registry.npm.taobao.org i express
        npm install
        npm i vue@3.3.13 
        npm run build
    """
}





def buildDockerImage(dockerfile="Dockerfile") {
    docker.withTool('docker') {
        docker.withServer(dockerServer){
            docker.withRegistry(dockerRegistry, dockerRegistryCredentialsId) {
                def buildDockerImage = docker.build(imageBuildTag,"-f ${dockerfile} . --build-arg build=${env.BUILD_ID} --build-arg commit=${appCommit}")
                buildDockerImage.push()
            }
        }
    }
}

def applyToK8s(ns="weintell",deploymentName,containerName) {
    sh "kubectl -n ${ns} set image deployment/${deploymentName} ${containerName}=${imageBuildTag} --record"
}