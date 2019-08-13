import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.3.41" apply false
	kotlin("plugin.spring") version "1.3.41" apply false
	kotlin("plugin.jpa") version "1.3.41" apply false
	id("org.springframework.boot") version "2.1.7.RELEASE" apply false
	java
}


allprojects {
	group = "com.piresdio.kafkaexample"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

dependencies {
/*	subprojects.forEach {
		archives(it)
	}*/
}

subprojects {
	tasks.withType<KotlinCompile>().configureEach {
		println ( "Configuring $name in project ${project.name}..." )
		kotlinOptions {
			//freeCompilerArgs = listOf("-Xjsr305=strict")
			languageVersion = "1.3"
			apiVersion = "1.3"
			jvmTarget = "1.8"
			javaParameters = true   // Useful for reflection.
		}
	}
}

