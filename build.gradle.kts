
import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.InputFile
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputFile
import java.math.BigDecimal
import java.math.RoundingMode

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

abstract class BumpDevVersionTask : DefaultTask() {
    @get:InputFile
    abstract val inputVersionFile: org.gradle.api.file.RegularFileProperty

    @get:OutputFile
    abstract val outputVersionFile: org.gradle.api.file.RegularFileProperty

    @TaskAction
    fun bump() {
        val file = inputVersionFile.get().asFile
        if (!file.exists()) {
            file.writeText("0.1\n")
        }

        val raw = file.readText().trim()
        val current = raw.toBigDecimalOrNull()
            ?.setScale(1, RoundingMode.HALF_UP)
            ?: error("Invalid dev-version.txt value '$raw'. Expected numeric format like 1.2")

        val next = current.add(BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP)
        outputVersionFile.get().asFile.writeText("${next.toPlainString()}\n")
        logger.lifecycle("Bumped dev-version.txt to ${next.toPlainString()}")
    }
}

val devVersionFile = layout.projectDirectory.file("dev-version.txt").asFile

fun readDevVersion(): BigDecimal {
    if (!devVersionFile.exists()) {
        devVersionFile.writeText("0.1\n")
    }
    val raw = devVersionFile.readText().trim()
    return raw.toBigDecimalOrNull()
        ?.setScale(1, RoundingMode.HALF_UP)
        ?: error("Invalid dev-version.txt value '$raw'. Expected numeric format like 1.2")
}

fun formatDevVersion(version: BigDecimal): String = version.setScale(1, RoundingMode.HALF_UP).toPlainString()

val currentDevVersion = readDevVersion()
val devJarBaseName = "gtnhadditions-dev"
val devJarVersion = formatDevVersion(currentDevVersion)
val cleanDevJars by tasks.registering(Delete::class) {
    delete(
        fileTree(layout.buildDirectory.dir("libs").get().asFile) {
            include("$devJarBaseName-*.jar")
            include("gtnh-additional-crafts-*.jar")
        })
}
val bumpDevVersion by tasks.registering(BumpDevVersionTask::class) {
    inputVersionFile.set(layout.projectDirectory.file("dev-version.txt"))
    outputVersionFile.set(layout.projectDirectory.file("dev-version.txt"))
}

tasks.named<Jar>("jar") {
    destinationDirectory.set(layout.buildDirectory.dir("tmp/devJar"))
    archiveBaseName.set(devJarBaseName)
    archiveVersion.set(devJarVersion)
}

tasks.named<ReobfuscatedJar>("reobfJar") {
    dependsOn(cleanDevJars)
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    archiveBaseName.set(devJarBaseName)
    archiveVersion.set(devJarVersion)
    archiveClassifier.set("")
}

tasks.named("build") {
    finalizedBy(bumpDevVersion)
}
