@file:Suppress("UnstableApiUsage")

import org.apache.tools.ant.taskdefs.condition.Os
import kotlin.reflect.KProperty
import kotlin.text.replace

plugins {
    id("fabric-loom")
    java
}

class CamelCaseConverter {
	operator fun <T> getValue(ref: Any?, property: KProperty<*>): T {
		val camelCase: String = property.name
			.replace(Regex("[A-Z]")) { "_" + it.value.lowercase() }

		providers.gradleProperty(camelCase)

		@Suppress("UNCHECKED_CAST")
		return project.property(camelCase) as T
	}
}

val projectCamel = CamelCaseConverter();

val lwjglVersion: String by projectCamel
val lwjglNatives = when {
    Os.isFamily(Os.FAMILY_UNIX) && !Os.isFamily(Os.FAMILY_MAC) -> "natives-linux"
    Os.isFamily(Os.FAMILY_WINDOWS) -> "natives-windows"
    Os.isFamily(Os.FAMILY_MAC) -> "natives-macos${if (Os.isArch("aarch64")) "-arm64" else ""}"
    else -> error("Unsupported OS")
}

val version: String by projectCamel
val group: String by projectCamel
val modName: String by projectCamel
base.archivesName = modName

val btaChannel: String by projectCamel
val btaVersion: String by projectCamel

val loaderVersion: String by projectCamel
val legacyLwjglVersion: String by projectCamel

val halplibeVersion: String by projectCamel
val modMenuVersion: String by projectCamel

val slf4jApiVersion: String by projectCamel
val log4jVersion: String by projectCamel
val guavaVersion: String by projectCamel
val gsonVersion: String by projectCamel
val commonsLang3Version: String by projectCamel

val javaVersion: Int by projectCamel

loom {
    noIntermediateMappings()
    customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${btaChannel}/v${btaVersion}/manifest.json")
}

repositories {
    mavenCentral()
	maven("https://jitpack.io")
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
    maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
    ivy("https://github.com/Better-than-Adventure") {
        patternLayout { artifact("[organisation]/releases/download/v[revision]/[module].jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-client/${btaChannel}/") {
        patternLayout { artifact("/v[revision]/client.jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-server/${btaChannel}/") {
        patternLayout { artifact("/v[revision]/server.jar") }
        metadataSources { artifact() }
    }
    ivy("https://piston-data.mojang.com") {
        patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
        metadataSources { artifact() }
    }
}

dependencies {
    minecraft("::${btaVersion}")
    mappings(loom.layered {})

	// https://piston-data.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar
    modRuntimeOnly("objects:client:43db9b498cb67058d2e12d394e6507722e71bb45")
    // If you do not need Halplibe you can comment out or delete this line.
    modImplementation("turniplabs:halplibe:${halplibeVersion}")
	modImplementation("turniplabs:modmenu-bta:${modMenuVersion}")
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")
	modImplementation("com.github.Better-than-Adventure:legacy-lwjgl3:${legacyLwjglVersion}")

	implementation(platform("org.lwjgl:lwjgl-bom:${lwjglVersion}"))
	implementation("org.slf4j:slf4j-api:${slf4jApiVersion}")

	implementation("com.google.guava:guava:${guavaVersion}")
	implementation("com.google.code.gson:gson:${gsonVersion}")

	implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
	implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
	implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
	implementation("org.apache.logging.log4j:log4j-1.2-api:${log4jVersion}")

	implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
	include("org.apache.commons:commons-lang3:${commonsLang3Version}")

	implementation("org.lwjgl:lwjgl:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-assimp:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-stb:${lwjglVersion}")

	runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.toString()
		targetCompatibility = javaVersion.toString()
		if (javaVersion > 8) options.release = javaVersion
	}
	withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
	withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
	withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
	named<Jar>("jar") {
		val rootLicense = layout.projectDirectory.file("LICENSE")
		val parentLicense = layout.projectDirectory.file("../LICENSE")
		val licenseFile = when {
			rootLicense.asFile.exists() -> {
				logger.lifecycle("Using LICENSE from project root: ${rootLicense.asFile}")
				rootLicense
			}
			parentLicense.asFile.exists() -> {
				logger.lifecycle("Using LICENSE from parent directory: ${parentLicense.asFile}")
				parentLicense
			}
			else -> {
				logger.warn("No LICENSE file found in project or parent directory.")
				null
			}
		}
		licenseFile?.let {
			from(it) {
				rename { original -> "${original}_${archiveBaseName}" }
			}
		}
	}
	processResources {
		inputs.property("modVersion", version)
		inputs.property("loaderVersion", loaderVersion)
		inputs.property("javaVersion", javaVersion)
		inputs.property("HalplibeVersion", halplibeVersion)
		inputs.property("modMenuVersion", modMenuVersion)
		filesMatching("fabric.mod.json") {
			expand(
				mapOf(
					"version" to version,
					"fabricloader" to loaderVersion,
					"halplibe" to halplibeVersion,
					"java" to javaVersion,
					"modmenu" to modMenuVersion
				)
			)
		}
		filesMatching("**/*.mixins.json") { expand(mapOf("java" to javaVersion)) }
	}
	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(javaVersion)
			vendor = JvmVendorSpec.ADOPTIUM
		}
		sourceCompatibility = JavaVersion.toVersion(javaVersion)
		targetCompatibility = JavaVersion.toVersion(javaVersion)
		withSourcesJar()
	}
}
// Removes LWJGL2 dependencies
configurations.configureEach { exclude(group = "org.lwjgl.lwjgl") }
