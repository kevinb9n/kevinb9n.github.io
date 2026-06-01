import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("application")
  id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

application {
  mainClass.set("site.kevinb9n.javafx.TrianglesKt")
}

javafx {
  version = "21.0.3"
  modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {
  implementation("com.google.guava:guava:33.2.1-jre")
  implementation(kotlin("reflect"))

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")
  testImplementation("com.google.truth:truth:1.4.4")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    showExceptions = true
    showStackTraces = true
  }
}
