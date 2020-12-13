#!groovy

def mvnCmd = "./mvnw  -s .mvn/wrapper/settings.xml"

node {

  checkout scm


stage('Build'){
    sh "${mvnCmd} clean compile"
  }

stage('Test'){
    sh "${mvnCmd} clean test"
    archiveTestResults("base/target/surefire-reports/TEST-*.xml")
}

if (params.MAKE_RELEASE){

    stage ('Deploy Snapshot') {
        sh "${mvnCmd} -Dmaven.test.skip"
    }

    stage ('Release') {
      try {
          sh "git checkout -b rc-${params.RELEASE_VERSION}"
          sh "${mvnCmd} release:clean"
          sh "${mvnCmd} release:prepare -Dusername=${params.GIT_USERNAME} -Dpassword=${params.GIT_PASSWORD}  -DreleaseVersion=  -Dtag=v.${params.RELEASE_VERSION} "
          sh "${mvnCmd} release:perform"
          sh "git checkout master"
          sh "git merge git merge rc-${params.RELEASE_VERSION}"
      } catch (Exception ex){
          sh "${mvnCmd} release:rollback"
          sh "git checkout master"
      }

    }

 }
}


def archiveTestResults(filePattern){
    junit keepLongStdio: true, testResults: filePattern
}
