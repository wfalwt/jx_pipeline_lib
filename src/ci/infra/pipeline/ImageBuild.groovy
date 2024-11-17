package ci.infra.pipeline;

class ImageBuild extends BaseBuild{
    String releaseImage
    String releaseTag
    boolean tagSource = false


    boolean getTagSource() {
        return tagSource
    }

    void setTagSource(boolean tagSource) {
        this.tagSource = tagSource
    }

    String getReleaseImage() {
        return releaseImage
    }

    void setReleaseImage(String releaseImage) {
        this.releaseImage = releaseImage
    }

    String getReleaseTag() {
        return releaseTag
    }

    void setReleaseTag(String releaseTag) {
        this.releaseTag = releaseTag
    }
    boolean checkValidation(){
        boolean  checkValid = true
        if(tagSource) {
            if (repository == null || repository.isEmpty()) {
                checkValid = false
                addCheckErrorMessage("build repository required,use <<ImageBuild.setRepository>> to set ")
            }
        }
        if(buildEnv == null || buildEnv.isEmpty()) {
            checkValid = false
            addCheckErrorMessage("build env node required,use <<ImageBuild.setBuildEnv>> to set")
        }
        if(buildImage == null || buildImage.isEmpty()){
            checkValid = false
            addCheckErrorMessage("build image required,use <<ImageBuild.setBuildImage>> to set")
        }
        if(releaseImage == null || releaseImage.isEmpty()) {
            checkValid = false
            addCheckErrorMessage("build image required,use <<ImageBuild.setReleaseImage>> to set")
        }
        if(!skipDeploy) {
            if (k8sDeployment == null || k8sDeployment.isEmpty()) {
                checkValid = false
                addCheckErrorMessage("build k8sDeployment required,use <<ImageBuild.setK8sDeployment>> to set")
            }
            if (k8sContainer == null || k8sContainer.isEmpty()) {
                checkValid = false
                addCheckErrorMessage("build k8sDeployment required,use <<ImageBuild.setK8sContainer>> to set")
            }
        }
        return checkValid
    }

}
