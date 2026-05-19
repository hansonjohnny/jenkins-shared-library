def call(String service, String accountId, String ecrRepoName, String region) {
    withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
        sh "git clone https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimb-platform.git"

        dir('nimb-platform') {
            sh """
                git config user.email "jenkins@nimbus.com"
                git config user.name "Jenkins"

                # update image tag in the service values file
                sed -i 's|tag:.*|tag: "${BUILD_NUMBER}"|' helm/values/${service}.yaml

                git add helm/values/${service}.yaml
                git commit -m "ci: update ${service} image to build ${BUILD_NUMBER}"
                git push https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimb-platform.git HEAD:main
            """
        }
    }
}