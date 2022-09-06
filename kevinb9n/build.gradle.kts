import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.6.21"
  id("application")
  id("org.openjfx.javafxplugin") version "0.0.10"
}

dependencies {
  implementation("com.google.guava:guava:30.1-jre")
  implementation(kotlin("reflect"))

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
  testImplementation("com.google.truth:truth:1.0")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(15))
  }
}

javafx {
  version = "18.0.2"
  modules = listOf("javafx.controls", "javafx.swing")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    showExceptions = true
    showStackTraces = true
  }
}
