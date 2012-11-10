package dsl

class DslScript {
String title
String content 
    static constraints = {
      content(maxSize: 10000)
    }
}
