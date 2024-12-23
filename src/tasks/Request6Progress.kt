package tasks

import contributors.*
import java.util.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    var repoContributors = Collections.synchronizedList(mutableListOf<User>())
    repos.forEach { repo ->
        val contributors = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
        repoContributors = (contributors + repoContributors).aggregate()
        updateResults(repoContributors, repos.size == repoContributors.size)
    }
}
