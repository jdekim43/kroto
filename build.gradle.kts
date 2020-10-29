group = "kr.jadekim"
version = "0.0.1"

dependencies {
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }

//    tasks {
//        compileKotlin {
//            kotlinOptions.jvmTarget = "1.8"
//        }
//        compileTestKotlin {
//            kotlinOptions.jvmTarget = "1.8"
//        }
//    }

    tasks.withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}