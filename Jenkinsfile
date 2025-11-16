pipeline {
    agent any
    environment {
        SERVICE = 'order'
        NAME = "luisepessoabastos/${env.SERVICE}"
        K8S_NAMESPACE = 'store-api'
    }
    stages {
        stage('Dependencies') {
            steps {
                build job: 'order', wait: true
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Build & Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credential',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'TOKEN')]) {
                    sh "docker login -u $USERNAME -p $TOKEN"
                    sh "docker buildx create --use --platform=linux/arm64,linux/amd64 --node multi-platform-builder-${env.SERVICE} --name multi-platform-builder-${env.SERVICE}"
                    sh "docker buildx build --platform=linux/arm64,linux/amd64 --push --tag ${env.NAME}:latest --tag ${env.NAME}:${env.BUILD_ID} -f Dockerfile ."
                    sh "docker buildx rm --force multi-platform-builder-${env.SERVICE}"
                }
            }
        }
        stage('Deploy to K8s') {
            steps {
                script {
                    def namespaceExists = sh(
                        script: "kubectl get namespace ${K8S_NAMESPACE} || echo 'not-found'",
                        returnStdout: true
                    ).trim()
                    
                    if (namespaceExists == 'not-found') {
                        sh "kubectl create namespace ${K8S_NAMESPACE}"
                    }
                    
                    sh "kubectl apply -f k8s/ -n ${K8S_NAMESPACE}"
                    sh "kubectl set image deployment/order order=${env.NAME}:${env.BUILD_ID} -n ${K8S_NAMESPACE}"
                    sh "kubectl rollout status deployment/order -n ${K8S_NAMESPACE}"
                }
            }
        }
    }
}

