import org.gradle.kotlin.dsl.*
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  kotlin("jvm") version "2.0.10"
  id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "dev.surly"
version = "0.1.0"

repositories {
  mavenCentral()
}

val githubApiVersion: String = "1.323"
val jgitVersion: String = "6.10.0.202406032230-r"
val junitVersion: String = "5.10.3"

dependencies {
  implementation("org.kohsuke:github-api:${githubApiVersion}")
  implementation("org.eclipse.jgit:org.eclipse.jgit:${jgitVersion}")

  testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

kotlin {
  jvmToolchain(21)
}

tasks.withType<Test> {
  useJUnitPlatform()
  failFast = true
  testLogging {
    events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STARTED)
    exceptionFormat = TestExceptionFormat.FULL
  }
}

mavenPublishing {
  coordinates("dev.surly", "minimal-github", "0.1.0")
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
  signAllPublications()
  pom {
    name.set("Minimal GitHub")
    description.set("Minimal GitHub API client")
    inceptionYear.set("2024")
    url.set("https://github.com/thesurlydev/minimal-github/")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        id.set("thesurlydev")
        name.set("Shane Witbeck")
        url.set("https://github.com/thesurlydev/")
      }
    }
    scm {
      url.set("https://github.com/thesurlydev/minimal-github/")
      connection.set("scm:git:git://github.com/thesurlydev/minimal-github.git")
      developerConnection.set("scm:git:ssh://git@github.com/thesurlydev/minimal-github.git")
    }
  }
}