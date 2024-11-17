
def call(globalConfigFile="config/global.json"){
    globalConfigVars =  new groovy.json.JsonSlurperClassic().parseText(libraryResource(globalConfigFile))
    return globalConfigVars
}