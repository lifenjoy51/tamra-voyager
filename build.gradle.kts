import com.soywiz.korge.gradle.korge

plugins {
    alias(libs.plugins.korge)
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        //maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

korge {
    id = "me.lifenjoy51.tamra.voyager"

    dependencyProject(":domain")

    dependencies {
        commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    }

// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    targetJs()
    //targetDesktop()
    //targetIos()
    //targetAndroidIndirect() // targetAndroidDirect()
    //targetAndroidDirect()
}

