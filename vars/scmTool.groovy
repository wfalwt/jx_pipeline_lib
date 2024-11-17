def checkoutSourceFromGit(repository,branchName,repositoryCredentialsId){
    def appSCM = checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branchName}"]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [],
            submoduleCfg: [],
            userRemoteConfigs:[[credentialsId: repositoryCredentialsId, url: repository]]
    ])
    println("commit id : ${appSCM.GIT_COMMIT}")
    return appSCM.GIT_COMMIT
}

def tagSourceToGit(repository,repositoryCredentialsId,commit,tagName,comments){
    checkout([
            $class: 'GitSCM',
            branches: [[name: commit]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [],
            submoduleCfg: [],
            userRemoteConfigs:[[credentialsId: repositoryCredentialsId, url: repository]]
    ])

    def repo = repository.replace("https://","")
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: repositoryCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
        sh """
           git tag -a ${tagName} -m "${comments}"
           git push https://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@${repo} ${tagName}
           """
    }

}