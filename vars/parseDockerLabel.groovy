def call(String label) {
    def buildInfoJson = new groovy.json.JsonSlurperClassic().parseText(label)
    Map buildInfo = [:]
    buildInfoJson.each { key, val ->
        if(key.contains("commit")) {
            println("git commit information found ${key} commit id ${val}")
            buildInfo.commitId = val
        }
        if(key.contains("build")) {
            println("image build information found ${key} build id ${val}")
            buildInfo.buildId = val
        }
    }
    return buildInfo
}