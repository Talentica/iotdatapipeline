pipeline{
    agent any
   
    tools{
	maven 'maven' 
}

    parameters {
	string(name: 'gitUrl', defaultValue: 'http://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
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
		sh "curl --cacert {env.WORKSPACE}/nexus.crt -u admin:admin123 -T IgniteSparkIoT/target/*-dependencies.jar ${params.repositoryUrl}/nexus/" 

                }
}


          }




}
