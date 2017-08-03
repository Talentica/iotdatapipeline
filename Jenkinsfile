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
				
	withCredentials([[$class:'UsernamePasswordBinding',credentialsId:'nexus_user',variable:'NEXUS_USER']
,[$class:'CertificateMultiBinding',credentialsId:'nexus',keystoreVariable:'CERT']]){
	sh "curl -v --cacert ${CERT} -u ${NEXUS_USER} -T IgniteSparkIoT/target/*-dependencies.jar ${params.repositoryUrl}nexus/" 
}
                }
}

	stage('Configfile Upload'){
		steps{
	withCredentials([usernameColonPassword(credentialsId:'nexus_user',variable:'NEXUS_USER'),]){	
	 sh "curl -v --cacert /var/jenkins_home/jobs/test1/nexus.crt -u ${NEXUS_USER} -T ignite-config.xml ${params.repositoryUrl}config_files/"
}
}



}

	stage('Build Image'){
		steps{
			script{
		for (int i = 0; i<10; ++i){echo "${i}"}

}

}}
	stage('Tag Image'){
		steps{
			script{
			println "Image Tag phase"
}
}

}
	stage('Image Push'){
		steps{
			script{
			println "Image pushing phase"
}
}

}		

	stage('Deploy'){
		steps{
			script{
			println "Deploying Application"
}
		
}

}
          }




}
