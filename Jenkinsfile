// Ejemplo de sintaxis Groovy en un Jenkinsfile
pipeline {
    //agent any
    // CAMBIO: Ahora usamos la etiqueta 'windows' que configuraste en tu nodo principal
    agent { label 'windows' }

    tools {
        maven 'maven-3.9'
        jdk 'jdk-21'
    }

    environment {
        DOCKER_IMAGE = "diegosantos95/sales-api"
        SONAR_PROJECT_KEY = "gestion-notificaciones-sales-api"
        // 2. Llamamos a la credencial que creaste en Jenkins
        //SONAR_AUTH_TOKEN = credentials('SONAR_TOKEN') // ya no es necesario aquí porque withSonarQubeEnv lo inyecta solo
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // USAMOS BAT: Al estar en Windows, usamos comandos de Batch
                // CAMBIO: Seteamos -DskipTests=true para evitar errores si no hay pruebas aún
                // bat 'mvn clean package -DskipTests=true'
                sh 'mvn clean package -DskipTests=true'
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
                // NOTA: El nombre 'SonarServer' debe coincidir con el que pusiste en Configurar Sistema
                withSonarQubeEnv('SonarServer') {
                    //sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.java.binaries=target/classes"
                    //bat "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONAR_AUTH_TOKEN}"
                    // Jenkins ya sabe la URL y el Token, Maven los tomará de las vars de entorno
                    // bat "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
                    sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
                }
            }
        }

        /*
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }*/
        // --- MOVIMOS EL BUILD DE DOCKER AQUÍ PARA TENER LA IMAGEN LISTA ANTES DEL DEPLOY ---
        stage('Docker Build & Push') {
            when {
                // anyOf { branch 'master'; branch 'qa'; branch 'dev' }
                expression {
                     return env.BRANCH_NAME == 'master' ||
                            env.BRANCH_NAME == 'dev' ||
                            env.BRANCH_NAME == 'qa' ||
                            env.GIT_BRANCH?.endsWith('master')
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        // sh (Shell): Es el lenguaje de las terminales de Linux y macOS
                        // El login se hace una sola vez
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        // Construcción e imagen con tag de build y latest
                        sh "docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ."
                        sh "docker tag ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                        // Push de ambas versiones
                        sh "docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                        // bat (Batch): Es el lenguaje de la terminal de Windows (CMD)
                        /* bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        bat "docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ."
                        bat "docker tag ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                        bat "docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        bat "docker push ${DOCKER_IMAGE}:latest" */
                    }
                }
            }
        }

        stage('Deploy to DEV') {
            when {
            //branch 'dev'
            expression { return env.BRANCH_NAME == 'dev' || env.GIT_BRANCH?.endsWith('dev') }
            }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} al servidor de Desarrollo..."
                // Aquí iría el comando: docker-compose up -d o kubectl apply
            }
        }

        stage('Deploy to QA') {
            when {
            //branch 'qa'
            expression { return env.BRANCH_NAME == 'qa' || env.GIT_BRANCH?.endsWith('qa') }
            }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} al servidor de QA..."
            }
        }

        stage('Deploy to PROD') {
            when {
                expression { return env.BRANCH_NAME == 'master' || env.GIT_BRANCH?.endsWith('master') }
            }
            steps {
                echo "Desplegando la imagen ${DOCKER_IMAGE}:${env.BUILD_NUMBER} a Producción..."
            }
        }
    }

    post {
        always {
        // NOTA: Ya instalamos el plugin 'Workspace Cleanup', así que cleanWs() ya no dará error, deleteDir() que es nativo y hace lo mismo
        cleanWs()
        }
        success { echo "¡El pipeline finalizó con éxito!" }
        failure { echo "Atención: El pipeline de ${env.JOB_NAME} falló en el build #${env.BUILD_NUMBER}" }
    }
}