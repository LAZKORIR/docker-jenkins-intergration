currentBuild.displayName= "docker-jenkins-integration-#"+currentBuild.number
pipeline {
   agent any
   tools {
         maven 'MAVEN_HOME'
         jdk 'JAVA_HOME'
       }
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
            sh "docker build  . -t  lazaruskorir95/docker-jenkins-integration:${DOCKER_TAG}"
         }
      }

       stage ('Push to Registry'){
          steps {
          withCredentials([string(credentialsId: 'docker-hub', variable: 'dockerHubPWD')]) {
             sh "docker login -u lazaruskorir95 -p ${dockerHubPWD}"
             sh "docker push lazaruskorir95/docker-jenkins-integration:${DOCKER_TAG}"
                }

              }
           }

//             stage ('Deploy to Kubernetes'){
//                      steps {
//                          sh  kubernetesDeploy (configs: "deployment.yml", kubeconfigId: "kubernetes")
//                          }
//                       }
   }

}

def getDockerTag () {
     def tag = sh  script: 'git rev-parse --short=8 HEAD', returnStdout: true
     return tag;
}