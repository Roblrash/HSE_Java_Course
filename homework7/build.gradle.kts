plugins {
    java
}

subprojects {
    apply(plugin = "java")

    group = "homework7"
    version = "1.0"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
