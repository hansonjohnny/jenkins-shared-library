def call(String appDir) {
    script {
        sh "mkdir -p ${WORKSPACE}/reports/owasp"
        sh "mkdir -p /var/lib/jenkins/.dependency-check-data"
    }
    dir(appDir) {
        dependencyCheck(
            odcInstallation: 'DP-Check',
            additionalArguments: """
                --scan . \
                --out ${WORKSPACE}/reports/owasp \
                --format XML \
                --format HTML \
                --prettyPrint \
                --nvdApiKey ${NVD_KEY} \
                --data /var/lib/jenkins/.dependency-check-data \
                --enableExperimental
            """
        )
    }
    dependencyCheckPublisher(
        pattern: 'reports/owasp/dependency-check-report.xml',
        failedTotalCritical: 1,
        failedTotalHigh: 5
    )
}