import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.undercouch.gradle.tasks.download.Download
import org.apache.commons.io.output.NullOutputStream

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.github.xenon"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.1.0")
    implementation("com.github.monun:kommand:+")
    implementation("com.github.monun:tap:+")
}

fun TaskContainer.createPaperJar(name: String, classifier: String = "", configuration: ShadowJar.() -> Unit) {
    create<ShadowJar>(name) {
        archiveBaseName.set(project.property("pluginName").toString())
        archiveVersion.set("") // For bukkit plugin update
        archiveClassifier.set(classifier)
        from(sourceSets["main"].output)
        configurations = listOf(project.configurations.implementation.get().apply { isCanBeResolved = true })
        configuration()
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }
    createPaperJar("debugJar", "DEBUG") {
        var dest = File(rootDir, ".debug/plugins")
        val pluginName = archiveFileName.get()
        val pluginFile = File(dest, pluginName)
        if (pluginFile.exists()) dest = File(dest, "update")

        doLast {
            copy {
                from(archiveFile)
                into(dest)
            }
        }
    }
}
