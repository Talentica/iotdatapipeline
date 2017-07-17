pipeline{
    agent any
    parameters {
	string(name: 'gitUrl', defaultValue: 'https://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'gitBranch', defaultValue: 'shubhasish', description: 'Git branch top checkout')

}
    stages{
       stage('GIT Checkout'){
		 steps{
		   echo 'Code Checkout from GIT'
		   git url:"${params.gitUrl}" branch:"${params.gitBranch}"
		   


}
                     }


          }




}
