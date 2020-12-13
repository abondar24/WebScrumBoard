#!groovy

def mvnCmd = "./mvnw  -s .mvn/wrapper/settings.xml"

node {

  checkout scm


stage('Build'){
    sh ".${mvnCmd} clean compile"
  }

stage('Test'){
    sh ".${mvnCmd} clean test"
    archiveTestResults("base/target/surefire-reports/TEST-*.xml")
}

if (params.MAKE_RELEASE){

    stage ('Deploy Snapshot') {
        sh ".${mvnCmd} -Dmaven.test.skip"
    }



 }
}


def archiveTestResults(filePattern){
    junit keepLongStdio: true, testResults: filePattern
}
