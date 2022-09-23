plugins {
	id("java")
	id("groovy")
	id("jacoco")
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.autonomousapps.dependency-analysis") version "1.2.0"
	id("com.google.cloud.tools.jib") version "3.2.1"
	id("com.coditory.integration-test") version "1.4.0"
	id("org.openapi.generator") version "6.0.0"
	id("org.sonarqube") version "3.4.0.2513"
}

group = "net.catenax.traceability"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}

	sourceSets {
		main {
			java.srcDirs("src/main/java", "${buildDir}/generated/sources/openapi/java/main")
		}
	}
}

configurations {
	compileOnly.get().extendsFrom(configurations["annotationProcessor"])
}

repositories {
	maven { url = uri("https://repo.spring.io/release") }
	mavenCentral()
}

sonarqube {
	properties {
		property("sonar.organization", "catenax-ng")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.projectKey", "catenax-ng_product-traceability-foss-backend")
		property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/jacoco/*.xml")
		property("sonar.cpd.exclusions", listOf(
				"src/main/java/net/catenax/traceability/assets/infrastructure/adapters/jpa/**",
			)
		)
		property("sonar.coverage.exclusions", listOf(
				"src/main/java/net/catenax/traceability/generated/**",
				"src/main/java/net/catenax/traceability/openapi/**",
				"src/main/java/net/catenax/traceability/TraceabilityApplication.java",
				"src/main/java/net/catenax/traceability/common/**",
				"src/main/java/net/catenax/traceability/assets/domain/model/**",
				"src/main/java/net/catenax/traceability/assets/infrastructure/**",
				"src/main/java/net/catenax/traceability/assets/config/**"
			)
		)
	}
}

val commonsCodecVersion = "1.15"
val groovyVersion = "3.0.10"
val spockBomVersion = "2.1-groovy-3.0"
val greenmailVersion = "1.6.9"
val springfoxVersion = "3.0.0"
val keycloakVersion = "19.0.2"
val feignVersion = "11.8"
val springCloudVersion = "2021.0.1"
val springBootSecurityOauth2Version = "2.6.8"
val jacksonDatabindNullableVersion = "0.2.2"
val scribejavaVersion = "8.0.0"
val findBugsVersion = "3.0.2"
val restitoVersion = "0.9.5"
val resilience4jVersion = "1.7.0"
val testContainersVersion = "1.17.3"
val schedlockVersion = "4.41.0"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

dependencies {
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:$springBootSecurityOauth2Version")

	implementation("org.springframework.data:spring-data-commons")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")

	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("io.github.openfeign:feign-okhttp:$feignVersion")
	implementation("io.github.openfeign:feign-jackson:$feignVersion")

	implementation("org.openapitools:jackson-databind-nullable:$jacksonDatabindNullableVersion")
	implementation("com.google.code.findbugs:jsr305:$findBugsVersion")
	implementation("com.github.ben-manes.caffeine:caffeine")

	implementation("com.github.scribejava:scribejava-core:$scribejavaVersion")

	implementation("org.keycloak:keycloak-spring-boot-starter:$keycloakVersion")

	implementation("io.springfox:springfox-boot-starter:$springfoxVersion")
	implementation("net.javacrumbs.shedlock:shedlock-spring:$schedlockVersion")
	implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$schedlockVersion")


	// for demo purposes, to be removed once EDC works
	implementation("com.github.javafaker:javafaker:1.0.2") {
		exclude("org.yaml")
	}

	implementation("commons-codec:commons-codec:$commonsCodecVersion")

	implementation("io.github.resilience4j:resilience4j-feign:${resilience4jVersion}")
	implementation("io.github.resilience4j:resilience4j-retry:${resilience4jVersion}")
	implementation("io.github.resilience4j:resilience4j-spring-boot2:${resilience4jVersion}")

	implementation("com.auth0:java-jwt:3.19.2")
	implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")

    testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
	testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
    testImplementation(platform("org.spockframework:spock-bom:$spockBomVersion"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")

	integrationImplementation("org.testcontainers:postgresql:$testContainersVersion")
	integrationImplementation("org.testcontainers:spock:$testContainersVersion")

	integrationImplementation("org.springframework.boot:spring-boot-starter-test")
	integrationImplementation("org.springframework.security:spring-security-test")

    integrationImplementation("com.icegreen:greenmail-spring:$greenmailVersion")
	integrationImplementation("com.xebialabs.restito:restito:$restitoVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.create<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateAasRegistryApi") {
	inputSpec.set("${project.rootDir}/openapi/aas-registry-openapi.yaml")
	outputDir.set("${buildDir}/openapi")
	validateSpec.set(false)

	groupId.set("${project.group}")

	library.set("feign")
	generatorName.set("java")
	apiPackage.set("net.catenax.traceability.assets.infrastructure.adapters.openapi.registry")
	modelPackage.set("net.catenax.traceability.assets.infrastructure.adapters.openapi.registry")
	configOptions.put("sourceFolder", "src/main/java")
}

tasks.withType<org.openapitools.generator.gradle.plugin.tasks.GenerateTask> {
	doLast {
		delete(fileTree("$buildDir/generated/sources/openapi"))
		copy {
			from(fileTree("${buildDir}/openapi/src/main/java"))
			into("$buildDir/generated/sources/openapi/java/main")
		}
	}
}

tasks.withType<JavaCompile> {
	dependsOn("generateAasRegistryApi")
}

tasks.jacocoTestReport {
	executionData.setFrom(fileTree("${project.buildDir}").include("/jacoco/*.exec"))
	reports {
		xml.required.set(true)
		xml.outputLocation.set(File("${project.buildDir}/jacoco/jacocoTestReport.xml"))
		csv.required.set(false)
		html.required.set(false)
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"net/catenax/traceability/generated/**",
					"net/catenax/traceability/openapi/**",
					"net/catenax/traceability/*Application.class",
					"net/catenax/traceability/common/**",
					"net/catenax/traceability/assets/domain/model/**",
					"net/catenax/traceability/assets/infrastructure/**",
					"net/catenax/traceability/assets/config/**"
				)
			}
		})
	)
}
