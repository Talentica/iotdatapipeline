pipeline{
    agent any
   
    tools{
	maven 'maven' 
}

    parameters {
	string(name: 'gitUrl', defaultValue: 'https://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'gitBranch', defaultValue: 'shubhasish', description: 'Git branch top checkout')
	string(name: 'pomPath', defaultValue: 'IgniteSparkIOT/pom.xml', description: 'path of pom.xml')

}
    stages{
	stage('Maven Build'){
	   steps{
		echo "Building Maven Project"
		sh "mvn -f IgniteSparkIoT/pom.xml clean install"
                }
}


          }




}
