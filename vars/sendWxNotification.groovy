def call(String message,String url){
    def builder = currentBuildUserId()
    def build_id = "${env.BUILD_ID}"
    def content ="${message} [本次构建者： ${builder} ，构建ID: ${build_id}]"
    def reqBody = '{"msgtype": "text","text": {"content": "' + content +'"}}'
    def post = new URL(url).openConnection()
    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(reqBody.getBytes("UTF-8"))
    def postRC = post.getResponseCode()
    if(postRC.equals(200)) {
        println("message ${message} send!")
    }
}