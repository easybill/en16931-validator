plugins {
    java
    id("io.quarkus")
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.spotbugs") version "6.0.15"
    id("org.checkerframework") version "0.6.44"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-smallrye-health")

    implementation ("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("com.helger.commons:ph-commons:11.1.6")
    implementation("com.helger.schematron:ph-schematron-api:7.1.0")
    implementation("com.helger.schematron:ph-schematron-xslt:8.0.0")
    implementation("commons-io:commons-io:2.17.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

group = "io.github.easybill"
version = ""

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

spotbugs {
    ignoreFailures = false
    showProgress = false
}

spotless {
    java {
        importOrder()
        prettier(mapOf(
            "prettier" to "2.8.8",
            "prettier-plugin-java" to "2.2.0"
        )).config(mapOf(
            "parser" to "java",
            "tabWidth" to 4,
        ))
        removeUnusedImports()
    }
}

checkerFramework {
    excludeTests = true
    suppressLombokWarnings = false
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
        "org.checkerframework.checker.resourceleak.ResourceLeakChecker",
        "org.checkerframework.checker.formatter.FormatterChecker",
    )
    extraJavacArgs = listOf(
        "-AsuppressWarnings=type.anno.before.decl.anno,type.anno.before.modifier"
    )
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
