def call(Map config) {
    pipeline {
        agent any

        tools { jdk 'jdk' }

        environment {
            SCANNER_HOME      = "${tool 'sonar-scanner'}"
            AWS_ACCOUNT_ID    = credentials('ACCOUNT_ID')
            AWS_ECR_REPO_NAME = credentials("${config.ecrCredId}")
            AWS_DEFAULT_REGION = "${config.awsRegion ?: 'us-east-2'}"
            REPOSITORY_URI    = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
            NVD_KEY           = credentials('nvdApiKey')
        }

        options {
            timeout(time: 120, unit: "MINUTES")
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: "10"))
        }

        stages {
            stage("Cleanup Workspace") {
                steps { cleanWs() }
            }

            stage("Checkout") {
                steps { checkout scm }
            }

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
                    dockerBuild(
                        config.appDir,
                        env.AWS_ECR_REPO_NAME
                    )
                }
            }

            stage("Trivy Image Scan") {
                steps { trivyScan(null, env.AWS_ECR_REPO_NAME) }
            }

            stage("Push to ECR") {
                steps {
                    ecrPush(
                        env.REPOSITORY_URI,
                        env.AWS_ECR_REPO_NAME,
                        env.AWS_DEFAULT_REGION
                    )
                }
            }

            stage("Update K8s Deployment") {
                steps {
                    updateK8sDeployment(
                        config.service,
                        env.AWS_ACCOUNT_ID ,
                        env.AWS_ECR_REPO_NAME ,
                        env.AWS_ECR_REPO_NAME
                    )
                }
            }
        }

        post { always { cleanWs() } }
    }
}