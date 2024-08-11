package dev.surly.github

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.kohsuke.github.GHFileNotFoundException
import org.kohsuke.github.GHRepository
import java.io.File

@Disabled
class GitHubTests {

  private val testProjectPath = "../test-output/java-lib"
  private val testRepoName = "minimal-github-test"
  private val testDescription = "description"
  private val login = System.getenv("GH_LOGIN")
  private val token = System.getenv("GH_TOKEN")
  private lateinit var git: GitHub

  @BeforeEach
  fun setup() {
    assertNotNull(login, "missing GH_LOGIN environment variable")
    assertNotNull(token, "missing GH_TOKEN environment variable")
    git = GitHub(login, token)
    assertNotNull(git)
  }

  @AfterEach
  fun tearDown() {
    // make sure to delete the test repo
    try {
      git.getRepository("$login/$testRepoName").delete()
      Thread.sleep(1000) // wait for repo to be deleted
    } catch (e: Exception) {
      // noOp
    }
  }

  @Test
  fun createAndInit() {
    val createdRepo: GHRepository = git.createRepository(testRepoName, testDescription, false)
    assertNotNull(createdRepo.id)
    assertEquals(testRepoName, createdRepo.name)
    assertTrue(createdRepo.isPrivate)

    Thread.sleep(1000) // wait for repo to be created

    git.initializeRepository(createdRepo.sshUrl, File(testProjectPath))
  }

  @Test
  fun crudRepo() {

    val git = GitHub(login, token)

    val createdRepo: GHRepository = git.createRepository(testRepoName, testDescription, false)
    assertNotNull(createdRepo.id)
    assertEquals(testRepoName, createdRepo.name)
    assertTrue(createdRepo.isPrivate)

    Thread.sleep(1000) // wait for repo to be created

    val fullName = createdRepo.fullName
    val repo = git.getRepository(fullName)
    assertEquals(testRepoName, repo.name)

    git.deleteRepository(fullName)

    Thread.sleep(1000) // wait for delete

    try {
      git.getRepository(fullName)
      fail<String>("Should throw not found!")
    } catch (nfe: GHFileNotFoundException) {
      // noOp
    }
  }
}
