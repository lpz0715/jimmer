plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "LocalMaven"
            url = uri("file:///E:/maven/mavenRepository")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            plugins.withType<JavaPlugin> {
                from(components["java"])
            }
            plugins.withType<JavaPlatformPlugin> {
                from(components["javaPlatform"])
            }
            pom {
                name.set("jimmer")
                description.set("The most advanced ORM of JVM, for both java & kotlin")
                url.set("https://github.com/babyfish-ct/jimmer")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://github.com/babyfish-ct/jimmer/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("babyfish-ct")
                        name.set("陈涛")
                        email.set("babyfish.ct@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/babyfish-ct/jimmer.git")
                    developerConnection.set("scm:git:ssh://github.com/babyfish-ct/jimmer.git")
                    url.set("https://github.com//babyfish-ct/jimmer")
                }
            }
        }
    }
}

//signing {
//    // 仅当发布到MavenCentral时签名，本地发布跳过
//    setRequired { gradle.taskGraph.hasTask("publishMavenJavaPublicationToMavenCentralRepository") }
//}