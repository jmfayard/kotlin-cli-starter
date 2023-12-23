pluginManagement {
    repositories {
        gradlePluginPortal()
        //mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshot")
    }
}
plugins {
    id("com.gradle.enterprise") version "3.16.1"
    id("de.fayard.refreshVersions") version "0.60.3"
    //id("com.louiscad.complete-kotlin") version "1.1.0"
}

// https://dev.to/jmfayard/the-one-gradle-trick-that-supersedes-all-the-others-5bpg
gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
        buildScanPublished {
            file("buildscan.log").appendText("${java.util.Date()} - $buildScanUri\n")
        }
    }

}

rootProject.name = "kotlin-cli-starter"
