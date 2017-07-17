pipeline{
    agent any
   
    tools{
	maven 'maven' 
}

    parameters {
	string(name: 'gitUrl', defaultValue: 'https://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'gitBranch', defaultValue: 'shubhasish', description: 'Git branch top checkout')
	string(name: 'pomPath', defaultValue: 'IgniteSparkIoT/pom.xml', description: 'path of pom.xml')

}
    stages{
	stage('Maven Build'){
	   steps{
		echo "Building Maven Project"
		sh "mvn -f IgniteSparkIoT/pom.xml clean install"
		script{
			def server = Artifactory.newServer(url:"http://172.19.103.71:8081/artifactory/",username="iotuser",password="iotuser")
			def uploadSpec = """{
  					      "files": [
   							 {
     							 "pattern": "IgniteSparkIoT/target/*-dependencies.jar",
      							 "target": "generic-local/"
   							 }
						        ]
					    }"""
			server.upload(uploadSpec)
}
                }
}


          }




}
