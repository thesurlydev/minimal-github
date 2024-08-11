package dev.surly.github

import dev.surly.scm.ScmService
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File

/**
 * See http://github-api.kohsuke.org/index.html
 * To create/manage GitHub access tokens go to: https://github.com/settings/tokens
 */
class GitHub(
  private val login: String,
  private val oAuthAccessToken: String,
) : ScmService<GHRepository> {

  private lateinit var github: GitHub

  override fun createRepository(
    name: String,
    description: String,
    public: Boolean,
  ): GHRepository {
    github = GitHub.connect(login, oAuthAccessToken)
    return github.createRepository(name)
      .description(description)
      .autoInit(true)
      .private_(!public)
      .create()
  }

  override fun initializeRepository(repositoryUrl: String, dir: File) {

    Git.init()
      .setDirectory(dir)
      .call()
      .use { git -> { println("Created a new repository at ${git.repository.directory}") } }

    val git: Git = Git.open(dir)
    git.add()
      .addFilepattern(".")
      .call()

    git.commit()
      .setMessage("initial commit")
      .call()

    val pushResults: Iterable<PushResult> = git.push()
      .setCredentialsProvider(UsernamePasswordCredentialsProvider(login, oAuthAccessToken))
      .setRemote(repositoryUrl)
      .setForce(true)
      .setPushAll()
      .setPushTags()
      .call()

    pushResults.forEach { x -> x.remoteUpdates.forEach { remoteUpdate -> println(remoteUpdate) } }
  }

  override fun deleteRepository(name: String) {
    getRepository(name).delete()
  }

  override fun getRepository(name: String): GHRepository {
    github = GitHub.connect(login, oAuthAccessToken)
    return github.getRepository(name)
  }
}
