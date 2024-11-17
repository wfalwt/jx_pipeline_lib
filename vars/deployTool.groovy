def applyToK8s(ns,deploymentName,containerName,image,returnText = false) {
    def cmd = "kubectl -n ${ns} set image deployment/${deploymentName} ${containerName}=${image} --record"
    if(returnText) {
        return cmd
    }else {
        sh "${cmd}"
    }
}

def applyBySSH(sshServerConfigName,execCommand) {
    sshPublisher(
        publishers: [
            sshPublisherDesc(
                configName: sshServerConfigName,
                transfers: [sshTransfer(excludes: '',execCommand: execCommand)],
                verbose: true
            )
        ]
    )
}