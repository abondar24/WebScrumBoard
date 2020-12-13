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

if (params.MAKE_RELEASE){

    stage ('Deploy Snapshot') {
        echo  "test"
        //sh "./mvnw clean deploy -s .mvn/wrapper/settings.xml -Dmaven.test.skip"
    }

    stage ('Release') {

       echo  "test"
    }

 }
}


def archiveTestResults(filePattern){
    junit keepLongStdio: true, testResults: filePattern
}
