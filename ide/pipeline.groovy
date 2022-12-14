pipeline {
    agent any
    triggers { pollSCM('* * * * *')}
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/pleasantstranga/jgsu-spring-petclinic.git'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                 }
                changed {
                    emailext attachLog: true, 
                        compressLog: true,
                        body: 'Please go to ${BUILD_URL} and verify the build', 
                        recipientProviders: [upstreamDevelopers()], 
                        subject: "Job \'${JOB_NAME}\' (build ${BUILD_NUMBER}) ${currentBuild.result} ", 
                        to: 'aaron.j.bernstein@ehi.com'                
                }
            }
        }
    }
}
