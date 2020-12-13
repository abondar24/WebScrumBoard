#!groovy


node {

  checkout scm


stage('Build'){

    sh "./mvnw clean compile"

  }

stage('Test'){
    sh "./mvnw clean test"
    archiveTestResults("base/target/surefire-reports/TEST-*.xml")
}



}

def archiveTestResults(filePattern){
    junit keepLongStdio: true, testResults: filePattern
}
