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

   }

}

def getDockerTag () {
     def tag = sh  script: 'git rev-parse HEAD', returnStdout: true
     return tag;
}