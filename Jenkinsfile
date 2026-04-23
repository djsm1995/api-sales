// Ejemplo de sintaxis Groovy en un Jenkinsfile
pipeline {
    agent any

    tools {
        maven 'maven-3.9'
        jdk 'jdk-21'
    }

    environment {
        DOCKER_IMAGE = "diegosantos95/sales-api"
        SONAR_PROJECT_KEY = "trade-sales-api"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
            post {
                always {
                    // junit '**/target/surefire-reports/*.xml'
                    echo 'Saltando tests por ahora...'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // --- MOVIMOS EL BUILD DE DOCKER AQUÍ PARA TENER LA IMAGEN LISTA ANTES DEL DEPLOY ---
        stage('Docker Build & Push') {
            when {
                anyOf { branch 'master'; branch 'qa'; branch 'dev' }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        // El login se hace una sola vez
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                        // Construcción e imagen con tag de build y latest
                        sh "docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ."
                        sh "docker tag ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}:latest"

                        // Push de ambas versiones
                        sh "docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        stage('Deploy to DEV') {
            when { branch 'dev' }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} al servidor de Desarrollo..."
                // Aquí iría el comando: docker-compose up -d o kubectl apply
            }
        }

        stage('Deploy to QA') {
            when { branch 'qa' }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} al servidor de QA..."
            }
        }

        stage('Deploy to PROD') {
            when { branch 'master' }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} a Producción..."
            }
        }
    }

    post {
        always { cleanWs() }
        success { echo "¡El pipeline finalizó con éxito!" }
        failure { echo "Atención: El pipeline de ${env.JOB_NAME} falló en el build #${env.BUILD_NUMBER}" }
    }
}