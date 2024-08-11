package dev.surly.scm

import java.io.File

interface ScmService<R> {
  fun createRepository(
    name: String,
    description: String,
    public: Boolean
  ): R

  fun initializeRepository(repositoryUrl: String, dir: File)
  fun deleteRepository(name: String)
  fun getRepository(name: String): R?
}
