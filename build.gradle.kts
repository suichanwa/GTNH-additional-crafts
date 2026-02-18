
import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.Delete

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.named<Jar>("jar") {
    destinationDirectory.set(layout.buildDirectory.dir("tmp/devJar"))
}

tasks.named<ReobfuscatedJar>("reobfJar") {
    destinationDirectory.set(layout.buildDirectory.dir("tmp/devJar"))
}

val stageFinalJar by tasks.registering(Copy::class) {
    from(layout.buildDirectory.dir("tmp/devJar")) {
        include("*.jar")
        exclude("*-dev.jar")
        exclude("*-sources.jar")
    }
    into(layout.buildDirectory.dir("libs"))
}

val pruneExtraJars by tasks.registering(Delete::class) {
    delete(
        fileTree(layout.buildDirectory.dir("libs").get().asFile) {
            include("*-dev.jar")
            include("*-sources.jar")
            include("mymodid-*.jar")
        })
}

tasks.named("build") {
    finalizedBy(stageFinalJar, pruneExtraJars)
}
