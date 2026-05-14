def call(String repoUri, String ecrRepoName, String region) {
    sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${repoUri}"
    sh "docker tag ${ecrRepoName} ${repoUri}${ecrRepoName}:${BUILD_NUMBER}"
    sh "docker push ${repoUri}${ecrRepoName}:${BUILD_NUMBER}"
}