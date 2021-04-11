import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.30"
}

dependencies {
  implementation("com.google.guava:guava:30.1-jre")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
  testImplementation("com.google.truth:truth:1.0")
  implementation(kotlin("reflect"))
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    showExceptions = true
    showStackTraces = true
  }
}
