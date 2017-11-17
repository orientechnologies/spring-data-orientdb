#!groovy
node("master") {
    properties([[$class: 'BuildDiscarderProperty', 
                 strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', 
                            artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']]])
    
    def mvnHome = tool 'mvn'
    def mvnJdk8Image = "orientdb/mvn-gradle-zulu-jdk-8"

    stage('Source checkout') {

        checkout scm
    }

    stage('Run tests on Java8') {
        docker.image("${mvnJdk8Image}").inside("--memory=4g ${env.VOLUMES}") {
            try {

                sh "${mvnHome}/bin/mvn  --batch-mode -V -U -fae  clean deploy -Dmaven.test.failure.ignore=true -Dsurefire.useFile=false"
            } catch (e) {
                currentBuild.result = 'FAILURE'

                slackSend(color: 'bad', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})\n${e}")
            } finally {
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'

            }


        }
    }

}

