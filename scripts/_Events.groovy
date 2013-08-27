import org.springframework.web.context.WebApplicationContext

eventSetClasspath = {
    rootLoader.addURL(new File("ext").toURI().toURL())    //only version that works with run-app
    //rootLoader.addURL(new File("TurtleExtension.groovy").toURI().toURL())
    //rootLoader.addURL(new File("ext/TurtleExtension.groovy").toURI().toURL())    WONT WORK
    //rootLoader.addURL(new File("WEB-INF/ext/TurtleExtension.groovy").toURI().toURL())
    //println "Root>>"+rootLoader
    //WebApplicationContext.getClassLoader().addURL(new File("ext").toURI().toURL())
    //WebApplicationContext.getClassLoader().addURL(new File("TurtleExtension.groovy").toURI().toURL())
    //WebApplicationContext.getClassLoader().addURL(new File("WEB-INF/ext"/TurtleExtension.groovy).toURI().toURL())
    //println "WebAPp>>"+WebApplicationContext.getClassLoader()

//    rootLoader.addURL(new File("lib/scala-reflect.jar").toURI().toURL())
//    rootLoader.addURL(new File("lib/scala-compiler.jar").toURI().toURL())
//    rootLoader.addURL(new File("lib/scala-library.jar").toURI().toURL())

    classpathSet = true //false
}


eventCreateWarStart = { warName, stagingDir ->
    // ..
    Ant.delete(
            dir:"${stagingDir}/js", includes:"**/*.js")
    Ant.delete(
            dir:"${stagingDir}/css" , includes:"**/*.css")
    Ant.copy(file: "dist/index.html",
            tofile: "${stagingDir}/index.html")
    Ant.copy(file: "dist/js/header.js",
            tofile: "${stagingDir}/js/header.js")
    Ant.copy(file: "dist/js/footer.js",
            tofile: "${stagingDir}/js/footer.js")
    Ant.copy(file: "dist/css/app.css",
            tofile: "${stagingDir}/css/app.css")
    Ant.copy(file: "dist/css/app.css",
            tofile: "${stagingDir}/css/app.css")

    println "copying ext....."
//    Ant.copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/WEB-INF/TurtleExtension.groovy")
//
//    Ant.copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/WEB-INF/ext/TurtleExtension.groovy")

//    Ant.copy(file: "ext/TurtleExtension.groovy",
//            tofile: "${stagingDir}/ext/TurtleExtension.groovy")

    println "stagingdir:::" + stagingDir
}


