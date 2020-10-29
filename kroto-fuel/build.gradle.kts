import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

plugins {
    kotlin("jvm") version "1.4.10"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "kroto-fuel"
val artifactGroup = "kr.jadekim"
val artifactVersion = "0.0.1"
group = artifactGroup
version = artifactVersion

dependencies {
    val fuelVersion: String by project

    api(project(":kroto-http"))

    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:$fuelVersion")
}

tasks.withType<KotlinCompile> {
    val jvmTarget: String by project

    kotlinOptions.jvmTarget = jvmTarget
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")

    publish = true

    setPublications("lib")

    pkg.apply {
        repo = "maven"
        name = "kroto-fuel"
        setLicenses("Apache2.0")
        setLabels("kotlin")
        vcsUrl = "https://github.com/jdekim43/kroto-fuel.git"
        version.apply {
            name = artifactVersion
            released = Date().toString()
        }
    }
}