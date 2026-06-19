def call(String service, String accountId, String ecrRepoName, String region) {
    withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
        sh "git clone https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimbus-platform.git"

        dir('nimbus-platform') {
            sh """
                git config user.email "jenkins@nimbus.com"
                git config user.name "Jenkins"

                # update image tag in the service's own chart values
                sed -i 's|tag:.*|tag: "${BUILD_NUMBER}"|' helm/${service}/values.yaml

                git add helm/${service}/values.yaml
                git commit -m "ci: update ${service} image to build ${BUILD_NUMBER}"
                git push https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimbus-platform.git HEAD:main
            """
        }
    }
}