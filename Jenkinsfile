#!groovy


node {

  checkout scm


stage('Build'){

    withMaven{
        sh "mvn clean install -DskipTests"
    }


  }


}
