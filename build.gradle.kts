plugins {
    `java-library`
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly(libs.io.papermc.paper.paper.api)
    api(libs.net.kyori.adventure.text.minimessage)
    api(libs.net.kyori.adventure.text.serializer.legacy)
    api(libs.org.bstats.bstats.bukkit)
}

group = "com.teunjojo"
version = "2.11.5-dev"
description = "SimpleAutoRestart"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    processResources {
        val pluginVersion = version
        filesMatching("plugin.yml") {
            expand("pluginVersion" to pluginVersion)
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}