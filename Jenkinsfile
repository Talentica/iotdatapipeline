pipeline{
    agent any
    parameters {
	string(name: 'git_url', defaultValue: 'https://github.com/Talentica/iotdatapipeline.git', description: 'Project Git Url')
	string(name: 'git_branch', defaultValue: 'shubhasish', description: 'Git branch top checkout')

}
    stages{
       stage('GIT Checkout'){
		 steps{
		   echo 'Code Checkout from GIT'
		   git url:"${params.git_url}" branch:"${params.git_branch}"
		   


}
                     }


          }




}
