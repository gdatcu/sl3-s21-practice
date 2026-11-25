pipeline {
    agent any

    tools {
        // Folosim numele definite in Tools (Capitolul 4, Pasul 4)
        maven 'Maven 3.8.x'
        // JDK 21 vine deja din imaginea jenkins/jenkins:lts-jdk21
    }

    triggers {
        // Jenkins local NU primeste webhook-uri de la GitHub,
        // asa ca verifica singur repo-ul la ~5 minute
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                // Repo-ul si branch-ul vin din configurarea job-ului
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // Credentiale din Jenkins (ID: hapify-creds)
                withCredentials([usernamePassword(
                        credentialsId: 'hapify-creds',
                        usernameVariable: 'HAPIFY_USER',
                        passwordVariable: 'HAPIFY_PASS')]) {
                    // Ignoram testele picate ca sa putem genera raportul
                    sh 'mvn clean test -Dmaven.test.failure.ignore=true'
                }
            }
        }
    }

    post {
        always {
            // Build GALBEN (UNSTABLE) daca pica teste
            junit testResults: 'target/surefire-reports/TEST-*.xml',
                  allowEmptyResults: true

            // Generare Raport Allure
            // commandline: Trebuie sa fie IDENTIC cu numele din Tools
            allure includeProperties: false,
                   jdk: '',
                   results: [[path: 'target/allure-results']],
                   commandline: 'Allure 2.x'

            // Arhivare Log-uri
            archiveArtifacts artifacts: 'logs/**/*.log',
                            allowEmptyArchive: true
        }
        unstable {
            echo 'Au picat teste. Verificati raportul Allure.'
        }
        failure {
            echo 'Build Failed. Verificati consola pentru detalii.'
        }
    }
}
