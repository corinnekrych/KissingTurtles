
### Pre-requesites
* Grails install. Because we are using TypeChecked we need at list v2.3
* Scala 2.11.0-M4
* sbt

### Septup
Clone Scala DSL repositories :
* https://github.com/pascohen/ScalaDSL
* https://github.com/pascohen/ScalaInterpreter
* https://github.com/pascohen/ScalaCompilerPlugin

### First time (one time only)
To get all Scala libraries, lift-son and continuations libs.
 setLib.sh

### Get update from Scala DSL
The Scala code for the DSL is under another repo. Given that you clone the 3 Scala repos, you can fetch and build the
latest just by running
  generateScalaArtefacts.sh


