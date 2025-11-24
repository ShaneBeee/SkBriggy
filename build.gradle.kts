plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

// Version of SkBriggy
val projectVersion = "1.5.6"
// Where this builds on the server
val serverLocation = "Skript/1-21-10"
// Minecraft version to build against
val minecraftVersion = "1.21.4"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    mavenLocal()

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    // Skript
    maven("https://repo.skriptlang.org/releases")

    // Command Api Snapshots
    maven("https://s01.oss.sonatype.org/content/repositories")

    // JitPack repo
    maven("https://jitpack.io")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:${minecraftVersion}-R0.1-SNAPSHOT")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.10.2")

    // SkBee
    compileOnly("com.github.ShaneBeee:SkBee:3.5.8")

    // Command Api
    implementation("dev.jorel:commandapi-paper-shade:11.0.1-SNAPSHOT")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

tasks {
    register("server", Copy::class) {
        dependsOn("shadowJar")
        from("build/libs") {
            include("SkBriggy-*.jar")
            destinationDir = file("/Users/ShaneBee/Desktop/Server/${serverLocation}/plugins/")
        }
    }
    processResources {
        expand("version" to projectVersion)
    }
    compileJava {
        options.release = 21
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    shadowJar {
        archiveFileName.set("SkBriggy-$projectVersion.jar")
        relocate("dev.jorel.commandapi", "com.shanebeestudios.briggy.api.commandapi")
        relocate("org.bstats", "com.shanebeestudios.briggy.metrics")
    }
    jar {
        dependsOn(shadowJar)
    }
}
