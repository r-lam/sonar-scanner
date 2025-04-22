@Library('devsecops_library') _

pipeline {
  agent {
    kubernetes {
      yaml '''
      apiVersion: v1
      kind: Pod
      metadata:
        labels:
          jenkins: worker
      spec:
        containers:
        - name: semgrep
          image: 061051214962.dkr.ecr.us-east-2.amazonaws.com/jenkins/build:v0.0.1
          resources:
            requests:
              cpu: 500m
              memory: 512Mi
          limits:
              cpu: 1000m
              memory: 2048Mi
          command: ["/bin/bash", "-c", "cat"]
          tty: true
        securityContext:
          runAsUser: 0
          fsGroup: 0
      '''
    }
  }
  environment {
    PROJECT = 'https://github.com/veracode/verademo.git'
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: "${PROJECT}"
      }
    }

    // stage('Análisis SonarQube') {
      // steps {
        // sonarScan()
      // }
    // }

    stage('Análisis Semgrep') {
      steps {
        container('semgrep') {
          sh '''
            echo "Ejecutando análisis Semgrep..."
            semgrep scan ${PROJECT_ROOT} \
              --config auto \
              --timeout-threshold 10000 \
              --json -output semgrep-result.json
          '''
        }
      }
    }
  }
}
