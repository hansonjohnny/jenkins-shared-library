def call(String appDir, String imageName) {
    sh "mkdir -p ${WORKSPACE}/reports/trivy"

    // Filesystem scan
    dir(appDir) {
        sh """
            trivy fs --severity LOW,MEDIUM --format table .
        """
        sh """
            trivy fs \
                --severity HIGH,CRITICAL \
                --exit-code 0 \
                --format json \
                --output ${WORKSPACE}/reports/trivy/fs-scan.json .
        """
    }

    // Image scan (only if imageName provided)
    if (imageName) {
        sh """
            trivy image --severity LOW,MEDIUM --format table ${imageName}
        """
        sh """
            trivy image \
                --severity HIGH,CRITICAL \
                --exit-code 0 \
                --format json \
                --output ${WORKSPACE}/reports/trivy/image-scan.json \
                ${imageName}
        """
    }
}