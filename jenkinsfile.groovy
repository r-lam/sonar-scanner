@Library('devsecops_library') _

pipeline {
  agent any
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
        sh '''
          echo "Ejecutando análisis Semgrep..."
          semgrep scan ${PROJECT_ROOT} \
            --config auto \
            --timeout-threshold 10000 \
            --json -output
        '''
      }
    }
  }
}
