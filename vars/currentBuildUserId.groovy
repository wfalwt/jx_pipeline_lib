def call(){
    def builder = "${currentBuild.getBuildCauses()[0].userId}"
    return builder
}