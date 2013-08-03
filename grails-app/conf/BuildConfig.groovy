grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits("global") {
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots/"
    }
    dependencies {
    }

    plugins {
        runtime ":jquery:1.8.2"
        //runtime ":cors:1.0.3"
        build ':tomcat:7.0.39'
        runtime ':hibernate:3.6.10.M3'
//        compile ":platform-core:1.0.M3"     //platform-core-1.0.RC5              //resources-1.2.RC3
        compile ":events-push:1.0.M7"     //m3
    }
}
