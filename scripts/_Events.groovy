eventSetClasspath = {
    rootLoader.addURL(new File("ext").toURI().toURL())

    rootLoader.addURL(new File("lib/scala-reflect.jar").toURI().toURL())
    rootLoader.addURL(new File("lib/scala-compiler.jar").toURI().toURL())
    rootLoader.addURL(new File("lib/scala-library.jar").toURI().toURL())

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
    println stagingDir
}


