package ci.infra.pipeline

class BaseBuild {

    protected String buildEnv
    protected String buildName
    protected String buildImage
    protected String buildDescription
    protected String k8sNamespace
    protected String k8sDeployment
    protected String k8sContainer
    protected String repository
    protected String repositoryCredentialsId
    protected boolean skipDeploy = false
    protected def checkErrorInfo = []
    protected String remoteServerName;


    String getBuildDescription() {
        return buildDescription
    }

    void setBuildDescription(String buildDescription) {
        this.buildDescription = buildDescription
    }

    String getBuildEnv() {
        return buildEnv
    }

    void setBuildEnv(String buildEnv) {
        if(buildEnv == "test" || buildEnv == "deploy"){
            this.buildEnv = buildEnv
        }else {
            throw new Exception("invalid build env " + buildEnv + ",available values :[test,deploy]")
        }
    }
    String getRepositoryCredentialsId() {
        return repositoryCredentialsId
    }

    void setRepositoryCredentialsId(String repositoryCredentialsId) {
        this.repositoryCredentialsId = repositoryCredentialsId
    }
    String getK8sNamespace() {
        return k8sNamespace
    }

    void setK8sNamespace(String k8sNamespace) {
        this.k8sNamespace = k8sNamespace
    }

    String getK8sDeployment() {
        return k8sDeployment
    }

    void setK8sDeployment(String k8sDeployment) {
        this.k8sDeployment = k8sDeployment
    }

    String getK8sContainer() {
        return k8sContainer
    }

    void setK8sContainer(String k8sContainer) {
        this.k8sContainer = k8sContainer
    }

    String getRepository() {
        return repository
    }

    void setRepository(String repository) {
        this.repository = repository
    }

    String getBuildName() {
        return buildName
    }

    void setBuildName(String buildName) {
        this.buildName = buildName
    }

    String getBuildImage() {
        return buildImage
    }

    void setBuildImage(String buildImage) {
        this.buildImage = buildImage
    }
    boolean getSkipDeploy() {
        return skipDeploy
    }

    void setSkipDeploy(boolean skipDeploy) {
        this.skipDeploy = skipDeploy
    }
    void addCheckErrorMessage(String errorMessage){
        this.checkErrorInfo << errorMessage
    }

    String[] getCheckErrorInfo() {
        return checkErrorInfo
    }

    String getRemoteServerName() {
        return remoteServerName
    }

    void setRemoteServerName(String remoteServerName) {
        this.remoteServerName = remoteServerName
    }
}
