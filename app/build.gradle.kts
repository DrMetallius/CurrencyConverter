plugins {
	id("com.android.application")
	kotlin("android")
}

val kotlinVersion: String by rootProject.extra

android {
	compileSdkVersion(28)
	defaultConfig {
		applicationId = "com.malcolmsoft.currencyconverter"
		minSdkVersion(21)
		targetSdkVersion(28)
		versionCode = 1
		versionName = "1.0.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	compileOptions {
		targetCompatibility = JavaVersion.VERSION_1_8
		sourceCompatibility = JavaVersion.VERSION_1_8
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")

	implementation("androidx.core:core-ktx:1.0.2")
	implementation("androidx.appcompat:appcompat:1.0.2")
	implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
	implementation("androidx.recyclerview:recyclerview:1.0.0")
	implementation("androidx.constraintlayout:constraintlayout:1.1.3")

	implementation("com.squareup.okhttp3:okhttp:3.14.1")

	androidTestImplementation("androidx.test:core:1.1.0")
	androidTestImplementation("androidx.test:runner:1.1.1")
	androidTestImplementation("androidx.test.ext:junit:1.1.0")
	androidTestImplementation("androidx.test:rules:1.1.1")
}
