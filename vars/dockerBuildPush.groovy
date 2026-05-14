def call(String appDir, String repoUri, String ecrRepoName, String region) {
    dir(appDir) {
        sh 'docker system prune -f'
        sh 'docker container prune -f'
        sh "docker build -t ${ecrRepoName} ."
    }
    sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${repoUri}"
    sh "docker tag ${ecrRepoName} ${repoUri}${ecrRepoName}:${BUILD_NUMBER}"
    sh "docker push ${repoUri}${ecrRepoName}:${BUILD_NUMBER}"
}