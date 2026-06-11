// Accepts either:
//   List<String>        — services under services/<name>/  (original behaviour)
//   Map<String,String>  — arbitrary service -> path prefix mapping
def call(def services) {
    def changedService = null

    // Build a normalised map of service -> prefix
    def prefixMap = [:]
    if (services instanceof Map) {
        prefixMap = services
    } else {
        services.each { svc -> prefixMap[svc] = "services/${svc}/" }
    }

    node {
        def changedFiles = sh(
            script: "git diff --name-only HEAD~1 HEAD",
            returnStdout: true
        ).trim().split('\n')

        for (entry in prefixMap) {
            if (changedFiles.any { it.startsWith(entry.value) }) {
                echo "Detected changes in: ${entry.key}"
                changedService = entry.key
                break
            }
        }
    }

    return changedService
}