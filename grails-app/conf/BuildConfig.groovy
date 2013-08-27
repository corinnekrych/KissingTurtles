grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

//grails.war.resources = { stagingDir, args ->
//
//    copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/WEB-INF/TurtleExtension.groovy")
//
//    copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/WEB-INF/ext/TurtleExtension.groovy")
//
//    copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/ext/TurtleExtension.groovy")
//}

grails.project.dependency.resolution = {

    inherits("global") {
        //excludes "atmosphere-runtime"
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
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
//        compile('org.atmosphere:atmosphere-runtime:1.1.0.RC4') {
//            excludes 'slf4j-api', 'atmosphere-ping'
//        }
        runtime 'postgresql:postgresql:8.4-702.jdbc3'
    }

    plugins {
        //runtime ":cors:1.0.3"
//        compile ":platform-core:1.0.M3"     //platform-core-1.0.RC5              //resources-1.2.RC3
        compile ":events-push:1.0.M7"     //m3

        build ":tomcat:7.0.41"
        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.M5" // or ":hibernate4:4.1.11.M2"
        runtime ":jquery:1.10.2"
//        runtime ":resources:1.2"
//        compile ":ui-performance:1.2.2"
//        runtime ":zipped-resources:1.0.1"
//        runtime ":cached-resources:1.1"
//        runtime ":yui-minify-resources:0.1.5"
    }
}
