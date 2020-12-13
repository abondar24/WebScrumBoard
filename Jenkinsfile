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

stage ('Deploy Snapshot') {
    sh "./mvnw clean deploy -s .mvn/wrapper/settings.xml -Dmaven.test.skip"
}



}

def archiveTestResults(filePattern){
    junit keepLongStdio: true, testResults: filePattern
}
