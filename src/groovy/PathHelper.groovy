class Path {
   def steps = []
   def Path(step) { steps<<step }
   def Path(stepsToCopy, step) { stepsToCopy.each { steps << it }; steps<<step }
 
   def add(step) { new Path(steps,step) }
   def last() { steps.last() }
   def contains(position) { steps.contains(position) }
   def length() { steps.size() }
 
   //String toString() { "Path = <<"+steps+">>\n" }
}

public class PathHelper {
   def walls
   def startPosition
   def endPosition

   public PathHelper(walls_,start_,end_) {
      this.walls = walls_
      this.startPosition = start_
      this.endPosition = end_
   }

   def contains(position) {
     walls.any {isAtPosition(it,position)}   
   }

   def isAtPosition(position,reference) {
      (position[0] == reference[0] && (position[1] == reference[1]))
   }

   def isAtStart(position) {
      isAtPosition(position,startPosition)
   }

   def isAtEnd(position) {
      isAtPosition(position,endPosition)
   }

   def validMove(position) {
      def result = []
      def positionUp = [position[0],position[1]+1]
      def positionDown = [position[0],position[1]-1]
      def positionLeft = [position[0]-1,position[1]]
      def positionRight = [position[0]+1,position[1]]
  
      if (!contains(positionUp)) result << positionUp
      if (!contains(positionDown)) result << positionDown
      if (!contains(positionLeft)) result << positionLeft
      if (!contains(positionRight)) result << positionRight
      result
   }

   def newStepsOfPath(path) {
      def newPaths = []
      if (isAtEnd(path.last())) return [path]
      
      def options = validMove(path.last())
      if (options.size() >0) {
         options.each { option -> if (!path.contains(option)) {
                               def newPath = path.add(option)
                               newPaths << newPath
                            }
                }
         newSteps(newPaths)
      } else {
         []
      }
   }

   def newSteps(paths) {
      def newPaths = []
      paths.each { p -> def newsteps = newStepsOfPath(p); if (newsteps.size()>0) newsteps.each { newPaths << it} else newPaths << p }
      newPaths
   }

   def findMinPath() {
      def path = [new Path(startPosition)]
      def allPaths = newSteps(path)
      def possiblePaths = []
      allPaths.each { if (it.contains(endPosition)) possiblePaths<<it}
      possiblePaths.min { it.length() }
   }
}