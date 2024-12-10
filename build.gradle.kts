plugins {
    kotlin("jvm") version "2.0.20"
    idea
    java
}

group = "tech.thatgravyboat"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Dependencies
    compileOnly(libs.mixins)
    compileOnly(libs.mixinextras)

    implementation(libs.ksp)
    implementation(libs.ktPoet)
    implementation(libs.kspPoet)
    implementation(libs.javaKtPoet)
    implementation(libs.javaPoet)

    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation(libs.mixins)
    testImplementation(libs.mixinextras)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}