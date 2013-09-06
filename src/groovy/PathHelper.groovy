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

   public PathHelper(walls_) {
      this.walls = walls_
   }

   private def contains(position) {
     walls.any {isAtPosition(it,position)}   
   }

   private def isAtPosition(position,reference) {
      (position[0] == reference[0] && (position[1] == reference[1]))
   }

   private def validMove(position) {
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

   private def newStepsOfPath(path,endPosition) {
      def newPaths = []
      if (isAtPosition(path.last(),endPosition)) return [path]
      
      def options = validMove(path.last())
      if (options.size() >0) {
         options.each { option -> if (!path.contains(option)) {
                               def newPath = path.add(option)
                               newPaths << newPath
                            }
                }
         newSteps(newPaths,endPosition)
      } else {
         []
      }
   }

   private def newSteps(paths,endPosition) {
      def newPaths = []
      paths.each { p -> def newsteps = newStepsOfPath(p,endPosition); if (newsteps.size()>0) newsteps.each { newPaths << it} else newPaths << p }
      newPaths
   }

   def findMinPath(startPosition,endPosition) {
      def path = [new Path(startPosition)]
      def allPaths = newSteps(path,endPosition)
      def possiblePaths = []
      allPaths.each { if (it.contains(endPosition)) possiblePaths<<it}
      possiblePaths.min { it.length() }
   }
}