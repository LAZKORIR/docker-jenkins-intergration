currentBuild.displayName= "docker-jenkins-intergration-#"+currentBuild.number
pipeline {
   agent any
   environment{
       DOCKER_TAG = getDockerTag()
   }
   stages {
      stage ('Build Jar file'){
          steps {
               sh "mvn clean package"
            }
         }
      stage ('Build Docker Image'){
         steps {
            sh "docker build  . -t lazaruskorir95/docker-jenkins-intergration:${DOCKER_TAG}"
         }
      }

       stage ('Push to Registry'){
          steps {
          withCredentials([string(credentialsId: 'docker-hub', variable: 'dockerHubPWD')]) {
              sh "docker login -u lazaruskorir95 -p ${dockerHubPWD}"
              sh "docker push lazaruskorir95/docker-jenkins-intergration:${DOCKER_TAG}"
                 }

              }
           }

            stage ('Deploy to Kubernetes'){
                     steps {
                         sh  kubernetesDeploy (configs: "deployment.yaml", kubeconfigId: "kubernetes")
                         }
                      }
   }

}

def getDockerTag () {
     def tag = sh  script: 'git rev-parse HEAD', returnStdout: true
     return tag;
}