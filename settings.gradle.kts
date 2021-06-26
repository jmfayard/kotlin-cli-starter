pluginManagement {
    repositories {
        gradlePluginPortal()
        //mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshot")
    }
}
plugins {
    id("com.gradle.enterprise") version "3.6.3"
    id("de.fayard.refreshVersions") version "0.10.1"
    id("com.louiscad.complete-kotlin") version "1.0.0"
}

// https://dev.to/jmfayard/the-one-gradle-trick-that-supersedes-all-the-others-5bpg
gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
        buildScanPublished {
            file("buildscan.log").appendText("${java.util.Date()} - $buildScanUri\n")
        }
    }

}

rootProject.name = "kotlin-cli-starter"
