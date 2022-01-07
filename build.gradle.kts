plugins {
    java
    id("fabric-loom") version "0.10-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower-mini") version "1.2.1"
    id("com.github.ben-manes.versions") version "0.41.0"
}

val minecraftVersion = "1.18.1"
val fabricLoaderVersion = "0.12.12"
val fabricApiVersion = "0.44.0+1.18"
val modmenuVersion = "3.0.0"
val multiconnectVersion = "1.5.6"

group = "com.mumfrey.worldeditcui"
version = "$minecraftVersion+01-SNAPSHOT"

repositories {
    // mirrors:
    // - https://maven.enginehub.org/repo/
    // - https://maven.terraformersmc.com/releases/
    // - https://maven.minecraftforge.net/
    // - https://maven.parchmentmc.org/
    maven(url = "https://repo.stellardrift.ca/repository/stable/") {
        name = "stellardriftReleases"
        mavenContent { releasesOnly() }
    }
    maven(url = "https://repo.stellardrift.ca/repository/snapshots/") {
        name = "stellardriftSnapshots"
        mavenContent { snapshotsOnly() }
    }
}

quiltflower {
    addToRuntimeClasspath.set(true)
}

val targetVersion = 17
java {
    sourceCompatibility = JavaVersion.toVersion(targetVersion)
    targetCompatibility = sourceCompatibility
    if (JavaVersion.current() < JavaVersion.toVersion(targetVersion)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetVersion))
    }
}

tasks.withType(JavaCompile::class) {
    options.release.set(targetVersion)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings {
            nameSyntheticMembers = false
        }
        parchment("org.parchmentmc.data:parchment-1.18.1:2021.12.19@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")
    modImplementation("net.earthcomputer.multiconnect:multiconnect-api:$multiconnectVersion") {
        isTransitive = false
    }

    // for development
    modImplementation("com.sk89q.worldedit:worldedit-fabric-mc1.18.1:7.2.8") {
        exclude("com.google.guava", "guava")
        exclude("com.google.code.gson", "gson")
        exclude("com.google.code.gson", "gson")
        exclude("it.unimi.dsi", "fastutil")
        exclude("org.apache.logging.log4j", "log4j-api")
    }
}

tasks {
    register("generateStandaloneRun") {
        description = "Generate a script that will run WorldEdit CUI, for graphics debugging"
        val scriptDest = project.layout.buildDirectory.file(if (System.getProperty("os.name").contains("windows", ignoreCase = true)) { "run-dev.bat" } else { "run-dev" })
        val argsDest = project.layout.buildDirectory.file("run-dev-args.txt")
        val taskClasspath = project.files(jar.map { it.outputs }, configurations.runtimeClasspath)
        val toolchain = project.javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(targetVersion)) }
        inputs.files(taskClasspath)
                .ignoreEmptyDirectories()
                .withPropertyName("runClasspath")
        // inputs.property("javaLauncher", toolchain)
        outputs.file(scriptDest)
        outputs.file(argsDest)
        doLast {
            val clientRun = minecraft.runConfigs.getByName("client")
            //-Dfabric.dli.config=${minecraft.devLauncherConfig.absolutePath} TODO
            argsDest.get().asFile.writeText("""    
                -Dfabric.dli.env=client
                -Dfabric.dli.main=${clientRun.defaultMainClass}
                ${clientRun.vmArgs.joinToString(System.lineSeparator())}
                -cp ${taskClasspath.asPath}
                net.fabricmc.devlaunchinjector.Main
                ${clientRun.programArgs.joinToString(System.lineSeparator())}
            """.trimIndent(), Charsets.UTF_8)
            scriptDest.get().asFile.writeText("""
                ${toolchain.get().executablePath.asFile.absolutePath} "@${argsDest.get().asFile.absolutePath}"
            """.trimIndent(), Charsets.UTF_8)
        }
    }

    withType(net.fabricmc.loom.task.AbstractRunTask::class).configureEach {
        // Mixin debug options
        jvmArgs(
                // "-Dmixin.debug.verbose=true",
                // "-Dmixin.debug.export=true",
                // "-Dmixin.debug.export.decompile.async=false", // to get decompiled sources when mixins straight up fail to apply
                "-Dmixin.dumpTargetOnFailure=true",
                "-Dmixin.checks.interfaces=true",
                "-Dwecui.debug.mixinaudit=true",
                "-Doptifabric.extract=true"
        )

        // Configure mixin agent
        jvmArgumentProviders += CommandLineArgumentProvider {
            // Resolve the Mixin configuration
            // Java agent: the jar file for mixin
            val mixinJar = configurations.runtimeClasspath.get().resolvedConfiguration
                    .getFiles { it.name == "sponge-mixin" && it.group == "net.fabricmc" }
                    .firstOrNull()

            if (mixinJar != null) {
                listOf("-javaagent:$mixinJar")
            } else {
                emptyList()
            }
        }
    }

    processResources.configure {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}
