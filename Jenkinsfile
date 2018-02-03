pipeline{
    agent any
   
    tools{
	maven 'maven' 
}

    parameters {
	string(name: 'gitUrl', defaultValue: 'http://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'gitBranch', defaultValue: 'spring_boot', description: 'Git branch top checkout')
	string(name: 'pomPath', defaultValue: 'IgniteSparkIoT/pom.xml', description: 'path of pom.xml')
	string(name: 'repositoryUrl', defaultValue: 'https://172.19.103.71:8443/nexus/repository/', description: 'repository url')
	string(name: 'wrapperUrl', defaultValue: 'http://172.19.103.71:5000/', description: 'wrapper application url')
	string(name: 'deploymentOption', defaultValue: 'Ignite', description: 'which component to deploy')

}

    stages{
	stage('Maven Build'){
	   steps{
		echo "Building Maven Project"
		sh "mvn -f ${params.pomPath} clean install"
		archiveArtifacts artifacts: 'target/IgniteSparkIOT*.jar', fingerprint: true
            }
        }

	stage('Docker Build'){
	    steps{
	    sh "ls"
	    sh "docker images"



	         }


	    }


	}




}
