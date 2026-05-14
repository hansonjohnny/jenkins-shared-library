def call(String projectKey, String projectName, String appDir) {
    withSonarQubeEnv('sonar-server') {
        sh "${SCANNER_HOME}/bin/sonar-scanner \
            -Dsonar.sources=. \
            -Dsonar.projectKey=${projectKey} \
            -Dsonar.projectName=${projectName}"
    }
    timeout(time: 10, unit: "MINUTES") {
        waitForQualityGate abortPipeline: true
    }
}