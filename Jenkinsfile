pipeline{
    agent any
   
    tools{
	maven 'maven' 
}

    parameters {
	string(name: 'gitUrl', defaultValue: 'https://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'gitBranch', defaultValue: 'shubhasish', description: 'Git branch top checkout')
	string(name: 'pomPath', defaultValue: 'IgniteSparkIoT/pom.xml', description: 'path of pom.xml')
	string(name: 'repositoryUrl', defaultValue: 'https://172.19.103.71:8443/nexus/repository/', description: 'repository url')

}
    stages{
	stage('Maven Build'){
	   steps{
		echo "Building Maven Project"
		sh "mvn -f IgniteSparkIoT/pom.xml clean install"
}}
	stage('Artifactory Upload'){
	   steps{
		script{
			def server = Artifactory.newArtifactoryServer(url:"${params.repositoryUrl}",credentialsId : "nexus")
			def uploadSpec = """{
  					      "files": [
   							 {
     							 "pattern": "IgniteSparkIoT/target/*-dependencies.jar",
      							 "target": "test/"
   							 }
						        ]
					    }"""
		def buildInfo = server.upload(uploadSpec)
}
                }
}


          }




}
