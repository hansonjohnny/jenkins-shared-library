def call(String appDir, String ecrRepoName) {
    dir(appDir) {
        sh 'docker system prune -f'
        sh 'docker container prune -f'
        sh "docker build -t ${ecrRepoName} ."
    }
}