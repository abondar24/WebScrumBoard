#!groovy


node {

  checkout scm


stage('Build'){

    sh "./mvnw clean install -DskipTests"

  }


}
