#!groovy

def mvnCmd = "./mvnw  -s .mvn/wrapper/settings.xml"
def pushCmd = "git push"
node {

    checkout scm


    stage('Build') {
        sh "${mvnCmd} clean compile"
    }

    stage('Test') {
        sh "${mvnCmd} clean test"
        archiveTestResults("base/target/surefire-reports/TEST-*.xml")
    }

    if (params.MAKE_RELEASE) {

        stage('Deploy Snapshot') {
            sh "${mvnCmd} deploy -Dmaven.test.skip"
        }

        stage('Release') {
            withCredentials([usernamePassword(credentialsId: 'gitCreds')]) {
                try {
                    sh "git checkout -b rc-${params.RELEASE_VERSION}"
                    sh "${pushCmd} origin rc-${params.RELEASE_VERSION}"
                    sh "${mvnCmd} release:clean"
                    sh "${mvnCmd} release:prepare -Dusername=${params.GIT_USERNAME} -Dpassword=${params.GIT_PASSWORD}  -DreleaseVersion=${params.RELEASE_VERSION}  -Dtag=v.${params.RELEASE_VERSION} -DupdateWorkingCopyVersions=false -DupdateDependencies=false"
                    sh "${mvnCmd} release:perform"
                    sh "git checkout master"
                    sh "git merge git merge rc-${params.RELEASE_VERSION}"
                    sh "git commit -a -m 'release ${params.RELEASE_VERSION}'"
                    sh $ { pushCmd }
                } catch (e) {

                    sh "git checkout master"
                    sh "git branch -d rc-${params.RELEASE_VERSION}"
                    sh "${pushCmd} origin --delete rc-${params.RELEASE_VERSION}"

                }
            }
        }

        stage('Deploy') {
            withKubeConfig([credentialsId: 'admin', serverUrl: 'https://127.0.0.1:16443']) {
                sh "../${mvnCmd}  -f base/pom.xml -Pkube -DskipTests fabric8:deploy"
            }

        }

        stage('Update development version') {
            sh "${mvnCmd} release:update-versions -DautoVersionSubmodules=true -DdevelopmentVersion=${params.DEV_VERSION}-SNAPSHOT"
            withCredentials([usernamePassword(credentialsId: 'gitCreds')]) {
                sh "git commit -a -m 'bump to ${params.DEV_VERSION}'"
                sh $ { pushCmd }
            }

        }

    }
}


def archiveTestResults(filePattern) {
    junit keepLongStdio: true, testResults: filePattern
}
