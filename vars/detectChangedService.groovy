def call(List services) {
    def changedService = null

    node {
        def changedFiles = sh(
            script: "git diff --name-only HEAD~1 HEAD",
            returnStdout: true
        ).trim().split('\n')

        for (service in services) {
            if (changedFiles.any { it.startsWith("services/${service}/") }) {
                echo "Detected changes in: ${service}"
                changedService = service
                break
            }
        }
    }

    return changedService
}