def call(Map config = [:]) {
    fullPipeline(
        service:         'frontend',
        appDir:          'frontend',
        k8sDir:          "k8s/frontend",
        sonarProjectKey:  config.sonarProjectKey  ?: 'cloud-native-frontend',
        sonarProjectName: config.sonarProjectName ?: 'cloud-native-frontend',
        ecrCredId:        config.ecrCredId        ?: 'ECR_REPO_FRONTEND',
        gitRepoName:      config.gitRepoName       ?: 'nimbus-platform',
        gitUserName:      config.gitUserName       ?: 'hansonjohnny',
        awsRegion:        config.awsRegion         ?: 'us-east-2'
    )
}
