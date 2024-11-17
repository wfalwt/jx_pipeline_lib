package ci.infra.pipeline

class ScmBuild extends BaseBuild{
    String repositoryBranch
    String dockerfile
    String buildTool
    String toolName
    String buildPath
    boolean buildWithTestSkip = true
    def supportBuildTools = ["python","php","maven","golang","node","java"]

    String getRepositoryBranch() {
        return repositoryBranch
    }

    void setRepositoryBranch(String repositoryBranch) {
        this.repositoryBranch = repositoryBranch
    }

    String getDockerfile() {
        return dockerfile
    }

    void setDockerfile(String dockerfile) {
        this.dockerfile = dockerfile
    }

    String getBuildTool() {
        return buildTool
    }

    void setBuildTool(String buildTool) {
        if(buildTool != null && supportBuildTools.contains(buildTool)) {
            this.buildTool = buildTool
        }else {
            throw new Exception("build tool not support " + buildTool + ",support tools " + supportBuildTools)
        }
    }

    boolean getBuildWithTestSkip() {
        return buildWithTestSkip
    }

    void setBuildWithTestSkip(boolean buildWithTestSkip) {
        this.buildWithTestSkip = buildWithTestSkip
    }

    String getBuildPath() {
        return buildPath
    }

    void setBuildPath(String buildPath) {
        this.buildPath = buildPath
    }

    String getToolName() {
        return toolName
    }

    void setToolName(String toolName) {
        this.toolName = toolName
    }

    boolean checkValidation(){
        boolean  checkValid = true
        if(this.repository == null || this.repository.isEmpty()){
            checkValid = false
            addCheckErrorMessage("build repository required,use <<ScmBuild.setRepository>> to set ")
        }
        if(this.buildEnv == null || this.buildEnv.isEmpty()) {
            checkValid = false
            addCheckErrorMessage("build env node required,use <<ScmBuild.setBuildEnv>> to set")
        }
        if(this.buildTool == null || this.buildTool.isEmpty()) {
            checkValid = false
            addCheckErrorMessage("build tool required,use <<ScmBuild.setBuildTool>> to set")
        }
        if(this.buildImage == null || this.buildImage.isEmpty()){
            checkValid = false
            addCheckErrorMessage("build image required,use <<ScmBuild.setBuildImage>> to set")
        }
        if(!this.skipDeploy) {
            if (this.k8sDeployment == null || this.k8sDeployment.isEmpty()) {
                checkValid = false
                addCheckErrorMessage("build k8sDeployment required,use <<ScmBuild.setK8sDeployment>> to set")
            }
            if (k8sContainer == null || k8sContainer.isEmpty()) {
                checkValid = false
                addCheckErrorMessage("build k8sDeployment required,use <<ScmBuild.setK8sContainer>> to set")
            }
        }
        return checkValid
    }

}
