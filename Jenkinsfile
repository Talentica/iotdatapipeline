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
	string(name: 'wrapperUrl', defaultValue: 'http://172.19.103.71:5001/', description: 'wrapper application url')

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
	sh "curl -v --cacert /var/jenkins_home/jobs/test1/nexus.crt --capath ${CERT} -u ${NEXUS_USER} -T IgniteSparkIoT/target/*-dependencies.jar ${params.repositoryUrl}nexus/" 
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
		sh "curl -v -X PUT -F dockerfile=@ignite_dockerfile ${params.wrapperUrl}/v1/api/image/build"

}}
	stage('Image Push'){
		steps{
		sh "curl -X POST -H \"Content-Type: application/json\" -X POST -d '{\"image\":\"registry:5000/ignite:latest\"}' ${params.wrapperUrl}/v1/api/image/push"
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
