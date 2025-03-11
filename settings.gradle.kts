pluginManagement {
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/jcenter")
//        maven("https://maven.aliyun.com/repository/google")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
//        maven("https://maven.aliyun.com/repository/spring")
//        maven("https://maven.aliyun.com/repository/spring-plugin")
//        maven("https://maven.aliyun.com/repository/grails-core")
//        maven("https://maven.aliyun.com/repository/apache-snapshots")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        maven("https://maven.aliyun.com/repository/central")
//        maven("https://maven.aliyun.com/repository/jcenter")
//        maven("https://maven.aliyun.com/repository/google")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
//        maven("https://maven.aliyun.com/repository/spring")
//        maven("https://maven.aliyun.com/repository/spring-plugin")
//        maven("https://maven.aliyun.com/repository/grails-core")
//        maven("https://maven.aliyun.com/repository/apache-snapshots")
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidDevToolbox"
include(":app")
//include(":dbinspector")
include(":TextEditor")
include(":axml")
