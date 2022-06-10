pipeline {
   agent any
   environment{
       DOCKER_TAG = getDockerTag()
   }
   stages {
      stage ('Build Docker Image'){
         steps {
            bat "docker build . -t lazaruskorir95/docker-jenkins-intergration:${DOCKER_ATG} "
         }
      }

   }

}

def getDockerTag () {
     def tag = bat  script: 'git rev-parse HEAD', returnStdout: true
     return tag;
}