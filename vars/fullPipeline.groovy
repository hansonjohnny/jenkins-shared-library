def call(Map config) {
    pipeline {
        agent any

        tools { jdk 'jdk' }

        environment {
            SCANNER_HOME        = "${tool 'sonar-scanner'}"
            AWS_ACCOUNT_ID      = credentials('ACCOUNT_ID')
            AWS_ECR_REPO_NAME   = credentials("${config.ecrCredId}")   // per-app credential
            AWS_DEFAULT_REGION  = "${config.awsRegion ?: 'us-east-2'}"
            REPOSITORY_URI      = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
            NVD_KEY             = credentials('nvdApiKey')
        }

        options {
            timeout(time: 120, unit: "MINUTES")
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: "10"))
        }

        stages {
            stage("Cleanup Workspace")  { steps { cleanWs() } }

            stage("Checkout")           { steps { checkout scm } }

            stage("Sonarqube Analysis") {
                steps {
                    sonarAnalysis(
                        config.sonarProjectKey,
                        config.sonarProjectName,
                        config.appDir
                    )
                }
            }

            stage("OWASP Scan") {
                steps { owaspScan(config.appDir) }
            }

            stage("Trivy FS Scan") {
                steps { trivyScan(config.appDir, null) }
            }

            stage("Docker Build") {
                steps {
                    dockerBuildPush(
                        config.appDir,
                        env.REPOSITORY_URI,
                        env.AWS_ECR_REPO_NAME,
                        env.AWS_DEFAULT_REGION
                    )
                }
            }

            stage("Trivy Image Scan") {
                steps { trivyScan(null, env.AWS_ECR_REPO_NAME) }
            }

            stage("Update K8s Deployment") {
                steps {
                    updateK8sDeployment(
                        config.k8sDir,
                        env.REPOSITORY_URI,
                        env.AWS_ECR_REPO_NAME,
                        config.gitRepoName,
                        config.gitUserName
                    )
                }
            }
        }

        post { always { cleanWs() } }
    }
}