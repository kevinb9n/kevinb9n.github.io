plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.30"
}

dependencies {
  implementation("com.google.guava:guava:30.1-jre")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.30")
  testImplementation("com.google.truth:truth:1.0")
    implementation(kotlin("reflect"))
}
