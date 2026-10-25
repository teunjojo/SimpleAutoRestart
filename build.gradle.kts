plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    // compileOnly("net.kyori:adventure-text-minimessage:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:5.2.0")
    
    // testImplementation("io.papermc.paper:paper-api:26.3.build.+")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")

    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3") 
	testRuntimeOnly("org.junit.platform:junit-platform-launcher") 

     testImplementation("net.bytebuddy:byte-buddy:1.17.8")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.8")
}

group = "com.teunjojo"
version = "2.11.6-dev"
description = "SimpleAutoRestart"

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

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    dependencies {
        exclude { it.moduleGroup != "org.bstats" }
    }

    relocate("org.bstats", project.group.toString())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")

    dependsOn(tasks.shadowJar)
    classpath = files(tasks.shadowJar.flatMap { it.archiveFile }) + classpath
}