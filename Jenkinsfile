/*
    This is jenkins template version 3
    maintainers @rkipkoech & @jwwambugu
*/
try {
    node {
      def app
      def owners

      stage('Clone Repository') {
        // clone repository and define environment specific variables.
        final scmVars = checkout(scm)
        env.BRANCH_NAME = scmVars.GIT_BRANCH
        env.SHORT_COMMIT = "${scmVars.GIT_COMMIT[0..7]}"
        env.GIT_REPO_NAME = scmVars.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')

        env.IMAGE = readMavenPom().getArtifactId()
        env.VERSION = readMavenPom().getVersion()
        env.NAMESPACE = "fs-business-payments"
        owners = getOwners()

      }

      stage('Run Java Unit Tests') {
        // Run maven build ignore failure to ensure report is always passed to sonarqube.
        docker.image('maven:3.6.3-openjdk-11').inside {
          sh "JAVA_HOME=/usr/local/openjdk-11/ && mvn -Dmaven.test.failure.ignore=true -Dhttp.proxyHost=proxy2.safaricom.net -Dhttp.proxyPort=8080 -Dhttps.proxyHost=proxy2.safaricom.net -Dhttps.proxyPort=8080  clean package"
        }
      }

      //run sonar analysis
      if (env.BRANCH_NAME == 'develop' || (env.BRANCH_NAME == 'master')) {

        stage('SonarQube code analysis') {
            withMaven(maven: 'M3') {
                withSonarQubeEnv('SonarQube') {
                    withEnv(["JAVA_HOME=${env.JAVA_11}"]) {
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }

      }

 if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master' ){

      stage("Check if Code has passed SonarQube Quality Gate") {

        sleep 20 //Some beauty sleep to make sure the quality gate is processed.
        timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout

          def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv

          if (qg.status != 'OK') {

            if (env.BRANCH_NAME == 'develop') {

              emailext (
                          subject: "Failed SonarQube Quality scans for '${env.GIT_REPO_NAME}' build number ' [${env.BUILD_NUMBER}]'",
                          body: """
                                  <p>B: Job ${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]:</p>
                                  <p>Kindly Confirm that the code quality for project '${env.GIT_REPO_NAME}' adheres to the Safaricom standards. For more information click on this link <a href='${env.BUILD_URL}/console'>${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]</a></p>
                                  <p>The console also has a link to web report </p>
                               """,
                          to: owners.maintainers.join(','),
                          mimeType: 'text/html'
                      )

            } else if (env.BRANCH_NAME == 'master') //kill the pipeline if master has issues
            {

              emailext (
                          subject: "Failed SonarQube Quality scans for '${env.GIT_REPO_NAME}' build number ' [${env.BUILD_NUMBER}]'",
                          body: """
                                  <p>B: Job ${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]:</p>
                                  <p>Kindly Confirm that the code quality for project '${env.GIT_REPO_NAME}' adheres to the Safaricom standards. For more information click on this link <a href='${env.BUILD_URL}/console'>${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]</a></p>
                                  <p>The console also has a link to web report </p>
                               """,
                          to: owners.maintainers.join(','),
                          mimeType: 'text/html'
                      )

              error "Pipeline aborted due to SonarQube quality gate failure: ${qg.status}"

            }
          }

          }
          
        }
        
      }
     


      if (env.BRANCH_NAME == 'master') {
        stage('Veracode SCA & SAST Scan Master Branch') {
          parallel(
            a: {
              // Policy scan
              withCredentials([usernamePassword(credentialsId: 'veracode-cred', usernameVariable: 'VERACODE_API_ID', passwordVariable: 'VERACODE_API_KEY')]) {
                veracode applicationName: 'Dxl-Build Microservices', criticality: 'VeryHigh',
                  fileNamePattern: '', pHost: 'proxy3.safaricom.net',
                  pPassword: '', pPort: '8080', pUser: '',
                  replacementPattern: '', scanExcludesPattern: '', scanIncludesPattern: '',
                  scanName: '$buildnumber - $timestamp', teams: 'DevSecOps', timeout: 15,
                  uploadExcludesPattern: '', uploadIncludesPattern: 'target/**.jar', waitForScan: true,
                  useProxy: true, vid: "${VERACODE_API_ID}", vkey: "${VERACODE_API_KEY}"
              }
            },
            b: {
              // 3rd party scan application
              withEnv(['https_proxy=http://proxy3:8080', 'scan_collectors=maven', 'compile_first=false', 'install_first=false']) {
                withMaven(maven: 'M3') {
                  withCredentials([string(credentialsId: 'SRCCLR_API_TOKEN', variable: 'SRCCLR_API_TOKEN')]) {
                    sh 'curl -sSL https://download.sourceclear.com/ci.sh | NOSCAN=1 sh'
                    sh 'mvn dependency:tree | /tmp/srcclr/bin/srcclr scan --stdin=maven --debug'
                  }
                }
              }
            }
          )
        }
      } else if ( env.BRANCH_NAME == 'develop') {
        stage('Veracode SCA & SAST Scan Development Branch') {
          parallel(
            a: {
              //Pipeline scan
              try {
                withEnv(["https_proxy=http://proxy3:8080"]) {
                  withCredentials([usernamePassword(credentialsId: 'veracode-cred', usernameVariable: 'VERACODE_API_ID', passwordVariable: 'VERACODE_API_KEY')]) {
                    sh 'curl -O https://downloads.veracode.com/securityscan/pipeline-scan-LATEST.zip'
                    sh 'unzip -o pipeline-scan-LATEST.zip pipeline-scan.jar'
                    sh ''
                    'java -Djava.net.useSystemProxies=true -jar pipeline-scan.jar -vid "$VERACODE_API_ID" -vkey "$VERACODE_API_KEY" --file target/**.jar --fail_on_severity="Very High, High" --fail_on_cwe="80" --timeout 15'
                    ''
                  }
                }
              } catch (Error | Exception e) {
                echo "failed but we continue"
              }
            },
            b: {
              // 3rd party scan application
              withEnv(['https_proxy=http://proxy3:8080', 'scan_collectors=maven', 'compile_first=false', 'install_first=false']) {
                withMaven(maven: 'M3') {
                  withCredentials([string(credentialsId: 'SRCCLR_API_TOKEN', variable: 'SRCCLR_API_TOKEN')]) {
                      sh 'curl -sSL https://download.sourceclear.com/ci.sh | NOSCAN=1 sh'
                      sh 'mvn dependency:tree | /tmp/srcclr/bin/srcclr scan --stdin=maven --debug --allow-dirty'
                  }
                }
              }
            }
          )
        }
      }

      stage('Build Docker Image') {
        app = docker.build("${env.NAMESPACE}/${env.GIT_REPO_NAME}")
      }

      /* Finally, we'll push the image:
       * Pushing multiple tags is cheap, as all the layers are reused.
       */

      if (env.BRANCH_NAME == 'staging' || env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'dev') {
        stage('Push Image to openshift uat Registry') {
          retry(3) {
            docker.withRegistry('https://ocr1.devocp.safaricom.net/', 'fs-business-payments-uat-habor-registry') {
              app.push("uat-${env.SHORT_COMMIT}")
            }
          }
        }
      } else if (env.BRANCH_NAME == 'master') {
        stage('Push Image to openshift prod Registry') {
          retry(3) {
            docker.withRegistry('https://ocr2.apps.hqocp.safaricom.net/', 'fs-business-payments-prod-habor-registry'){
              env.VERSION = version()
              app.push("v${env.Version}_${env.SHORT_COMMIT}")
              app.push("latest")

            }
          }
        }
      }

    }
}
    
catch (Error | Exception e) {
    echo 'Err: Incremental Build failed with Error: ' + e.toString()

              emailext(
                          subject: "Failed jenkins pipeline for '${env.GIT_REPO_NAME}' build number ' [${env.BUILD_NUMBER}]'",
                          body: """
                                  <p>B: Job ${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]:</p>
                                  <p>an exception occured while running the pipeline. For more information click on this link <a href='${env.BUILD_URL}/console'>${env.GIT_REPO_NAME} [${env.BUILD_NUMBER}]</a></p>

                               """,
                          to: owners.devops.join(','),
                          mimeType: 'text/html'
                      )
    throw e
  } finally {
    // Post build steps here
    /* Success or failure, always run post build steps */
    // send email
    // publish test results etc etc
  }
  def version() {
    pom = readMavenPom file: 'pom.xml'
    return pom.version
  }
  def getOwners() {
    owners = readYaml file: 'OWNERS.yml'
    return owners
  }
  def imageString(registry, name, tag_prefix, tag) {
    return "${registry}/${name}:${tag_prefix}${tag}"
  }
