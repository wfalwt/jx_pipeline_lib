def buildCodeWithMaven(mavenToolName="maven",jdkToolName="jdk-1.8",skipTest=true) {
    def mavenPath = tool mavenToolName
    println(mavenPath)
    def jdkPath = tool jdkToolName
    println(jdkPath)
    env.PATH="${mavenPath}/bin:${jdkPath}/bin:${env.PATH}"
    sh "mvn clean"
    sh "mvn package -Dmaven.test.skip=${skipTest}"
}

def buildDockerImage(dockerServer,dockerRegistry,dockerRegistryCredentialsId,imageBuildTag,appCommit,dockerToolName="docker",dockerfile="Dockerfile") {
    docker.withTool(dockerToolName) {
        docker.withServer(dockerServer){
            docker.withRegistry(dockerRegistry, dockerRegistryCredentialsId) {
                def buildDockerImage = docker.build(imageBuildTag,"-f ${dockerfile} . --build-arg build=${env.BUILD_ID} --build-arg commit=${appCommit}")
                buildDockerImage.push()
            }
        }
    }
}

def buildCodeWithNode(nodeName="node18",buildPath="src") {
    def nodePath = tool name: nodeName, type: 'nodejs'
    env.PATH="${nodePath}/bin:${env.PATH}"
    sh """
        cd ${buildPath}
        make build
    """
}

def buildCodeWithNodeInDocker(nodeName="node20",dockerToolName="docker",buildPath="src") {
    def nodeVars = globalVars "config/node.json"
    def nodeImage = nodeVars ."${nodeName}.image"
    withDockerContainer(args: '-u root -v $HOME/.m2:/root/.m2 -v $WORKSPACE:/root/source', image: "${nodeImage}", toolName: "${dockerToolName}") {
        sh """  
            node -v
            cd /root/source
            cd ${buildPath}
            npm config set strict-ssl false
            npm --registry https://registry.npm.taobao.org i express
            npm install
            npm run build
		"""
    }
}


def buildCodeWithGolang(golangToolName="go17",buildPath) {
    def goPath = tool golangToolName
    withEnv(["GOROOT=${goPath}/go", "PATH+GO=${goPath}/go/bin"]) {
        sh """
            cd ${buildPath}
            make build
        """
    }
}

def tagDockerImage(dockerServer,dockerRegistry,dockerRegistryCredentialsId,dockerToolName="docker",sourceImage,destImage) {
    docker.withTool(dockerToolName) {
        docker.withServer(dockerServer) {
            docker.withRegistry(dockerRegistry, dockerRegistryCredentialsId) {
                def buildDockerImage = docker.image(sourceImage)
                buildDockerImage.pull()
                sh "docker tag ${sourceImage} ${destImage}"
                def releaseImage = docker.image(destImage)
                releaseImage.push()
            }
        }
    }
    def buildInfo = sh(script: "docker -H ${dockerServer} inspect ${destImage} -f '{{json .Config.Labels }}'", returnStdout: true)
    return buildInfo
}