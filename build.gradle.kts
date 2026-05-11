plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}

// Version of SkBriggy
val projectVersion = "1.8.0"
// Where this builds on the server
val serverLocation = "Skript/26-1"
// Minecraft version to build against
val minecraftVersion = "26.1.2"

java.sourceCompatibility = JavaVersion.VERSION_25

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
    compileOnly("io.papermc.paper:paper-api:${minecraftVersion}.build.+")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.15.0")

    // SkriptRegistration
    implementation("com.github.ShaneBeee:SkriptRegistration:1.2.0")

    // SkBee
    compileOnly("com.github.ShaneBeee:SkBee:3.23.0@jar")

    // Command Api
    implementation("dev.jorel:commandapi-paper-shade:11.2.0")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

tasks {
    register("server", Copy::class) {
        dependsOn("shadowJar")
        from("build/libs") {
            include("SkBriggy-*.jar")
            destinationDir = file("/Users/ShaneBee/Desktop/Server/Minecraft/${serverLocation}/plugins/")
        }
    }
    processResources {
        expand("version" to projectVersion)
    }
    compileJava {
        options.release.set(21)
        // This allows the compiler to see "newer" classes even if targeting an older version
        options.isIncremental = false
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    shadowJar {
        archiveFileName.set("SkBriggy-$projectVersion.jar")
        archiveClassifier.set("")
        relocate("dev.jorel.commandapi", "com.shanebeestudios.briggy.api.commandapi")
        relocate("org.bstats", "com.shanebeestudios.briggy.metrics")
        relocate("com.github.shanebeee.skr", "com.shanebeestudios.briggy.registration")
    }
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }
}
