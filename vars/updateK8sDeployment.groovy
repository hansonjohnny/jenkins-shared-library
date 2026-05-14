// def call(String service, String repoUri, String ecrRepoName) {
//     withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
//         // checkout the platform repo
//         sh """
//             git clone https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimb-platform.git
//         """
//         dir("nimb-platform/k8s/${service}") {
//             sh """
//                 git config user.email "hansonjohnny648@gmail.com"
//                 git config user.name "hansonjohnny"

//                 sed -i "s|image:.*|image: ${repoUri}${ecrRepoName}:${BUILD_NUMBER}|g" deployment.yaml

//                 git add deployment.yaml
//                 git commit -m "ci: update ${service} image to build ${BUILD_NUMBER}"
//                 git push https://hansonjohnny:\${GITHUB_TOKEN}@github.com/hansonjohnny/nimb-platform.git HEAD:main
//             """
//         }
//     }
// }