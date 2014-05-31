
### Pre-requesites
* Grails install. Because we are using TypeChecked we need at least v2.3
* Scala 2.11.0-M4
* sbt

### Septup
#### Step1: create ```scala-dsl``` directory at the same level of ```KissingTurtles```
```
> mkdir scala-dsl
```

#### Step2: clone Scala DSL repositories :

```
> cd scala-dsl
> git clone https://github.com/pascohen/ScalaDSL
> git clone https://github.com/pascohen/ScalaInterpreter
> git clone https://github.com/pascohen/ScalaCompilerPlugin
```

#### Step3: First time (one time only)
To get all Scala libraries, lift-son and continuations libs.
``` 
> setLib.sh
```

#### Get update from Scala DSL
The Scala code for the DSL is under another repo. Given that you clone the 3 Scala repos, you can fetch and build the
latest just by running
```  
> generateScalaArtefacts.sh
```

