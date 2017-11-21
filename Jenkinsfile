#!groovy
node("master") {
    properties([[$class: 'BuildDiscarderProperty', 
                 strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', 
                            artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10']]])
    
    def mvnHome = tool 'mvn'
    def mvnJdk8Image = "orientdb/mvn-gradle-zulu-jdk-8"
    
    def containerName = env.JOB_NAME.replaceAll(/\//, "_") + 
            "_build_${currentBuild.number}"
			
    def appNameLabel = "docker_ci";
    def taskLabel = env.JOB_NAME.replaceAll(/\//, "_")


    stage('Source checkout') {

        checkout scm
    }

    stage('Run tests on Java8') {
        docker.image("${mvnJdk8Image}").inside("--label collectd_docker_app=${appNameLabel} --label collectd_docker_task=${taskLabel} " + 
                                               "--name ${containerName} --memory=4g ${env.VOLUMES}") {
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

