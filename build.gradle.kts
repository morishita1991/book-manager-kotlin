import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	id("org.flywaydb.flyway") version "8.0.1"
	id("nu.studer.jooq") version "7.1.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.flywaydb:flyway-core")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.flywaydb:flyway-mysql")

	runtimeOnly("com.mysql:mysql-connector-j")
	jooqGenerator("com.mysql:mysql-connector-j")
	jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
}

flyway {
	url = "jdbc:mysql://127.0.0.1:3306/sample-db?characterEncoding=utf8"
	user = "sample-user"
	password = "sample-pass"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jooq {
	configurations {
		create("main") {
			jooqConfiguration.apply {
				jdbc.apply {
					url = "jdbc:mysql://127.0.0.1:3306/sample-db?characterEncoding=utf8"
					user = "sample-user"
					password = "sample-pass"
				}
				generator.apply {
					name = "org.jooq.codegen.KotlinGenerator"
					database.apply {
						name = "org.jooq.meta.mysql.MySQLDatabase"
						inputSchema = "sample-db"
						excludes = "flyway_schema_history"
					}
					generate.apply {
						isDeprecated = false
						isTables = true
					}
					target.apply {
						packageName = "com.example.ktknowledgeTodo.infra.jooq"
						directory = "${buildDir}/generated/source/jooq/main"
					}
				}
			}
		}
	}
}
