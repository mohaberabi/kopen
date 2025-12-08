plugins {
    kotlin("jvm")
    `java-library`
}

group = "org.mohaberabi.make-open"
version = "1.0.0"


dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kotlin.compiler.embeddable)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}