def call(String appDir, String imageName) {
    sh "mkdir -p ${WORKSPACE}/reports/trivy"

    if (appDir) {
        dir(appDir) {
            sh """
                trivy fs --severity LOW,MEDIUM --format table .
            """
            sh """
                trivy fs \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 \
                    --format json \
                    --output ${WORKSPACE}/reports/trivy/fs-scan.json .
            """
        }
    }

    if (imageName) {
        sh """
            trivy image --severity LOW,MEDIUM --format table ${imageName}
        """
        sh """
            trivy image \
                --severity HIGH,CRITICAL \
                --exit-code 1 \
                --format json \
                --output ${WORKSPACE}/reports/trivy/image-scan.json \
                ${imageName}
        """
    }
}