import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

plugins {
    kotlin("multiplatform")
    // @Serializable이 붙은 클래스를 자동 변환한다.
    kotlin("plugin.serialization")
}

group = "me.lifenjoy51.tamra"
version "0.1"

dependencies {
    commonMainApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
            }
        }
    }
    // gradle project :domain:unspecified
    js(IR) {// or: IR, LEGACY, BOTH
        browser()
        nodejs()
        // https://youtrack.jetbrains.com/issue/KT-41382
        val main: KotlinJsCompilation by compilations {
            NamedDomainObjectCollectionDelegateProvider.of(this) {
                kotlinOptions {
                    //metaInfo = true
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation(kotlin("stdlib-common"))
            }
        }
        val jvmMain by getting
        val jsMain by getting
    }

}
