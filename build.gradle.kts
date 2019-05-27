buildscript {
	val kotlinVersion by extra("1.3.31")

	repositories {
		google()
		jcenter()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:3.4.1")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
	}
}

allprojects {
	repositories {
		google()
		jcenter()
	}
}

tasks.register("clean", Delete::class) {
	delete(rootProject.buildDir)
}
