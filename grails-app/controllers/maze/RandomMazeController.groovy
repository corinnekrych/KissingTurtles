package maze

import grails.converters.JSON

class RandomMazeController {

    def index(int length) {
		
		//generate a maze
		//int length = 15	
		def maze = new RandomMazeGenerator().depthFirstMaze2(length)	
	
		//	def maze = randomMaze(length)
		
		//a basic view to see the result
		render('<div style="color:#777777">')
		maze.each { tile ->
			
			if (tile.valid == false){
				render('<div style="background-color:#777777;width:20px;height:20x;border:1px solid #777777;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}else{
				render('<div style="background-color:#FFFFFF;color:#FFFFFF;width:20px;height:20x;border:1px solid #FFFFFF;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}
				
		}
		//render maze as JSON
		render('</div>')
		
	}
	
	def fastmaze(int length) {
		
		//generate a maze
		if(!length){
			length = 15
		}
			
		def maze = new RandomMazeGenerator().depthFirstMaze2(length)	
	
		//	def maze = randomMaze(length)
		
		//a basic view to see the result
		render('<div style="color:#777777">')
		maze.each { tile ->
			
			if (tile.valid == false){
				render('<div style="background-color:#777777;width:20px;height:20x;border:1px solid #777777;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}else{
				render('<div style="background-color:#FFFFFF;color:#FFFFFF;width:20px;height:20x;border:1px solid #FFFFFF;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}
				
		}
		render('</div>')	
	}
	
	def fastjson(int length) {	
		
		if(!length){
			length = 15
		}
		
		def maze = new RandomMazeGenerator().depthFirstMaze2(length)
		
		render('<div>')
		render maze as JSON
		render('</div>')
		
	}
	
	def slowmaze(int length) {
		
		//generate a maze
		if(!length){
			length = 15
		}
			
		def maze = new RandomMazeGenerator().depthFirstMazeSlow(length)
	
		//	def maze = randomMaze(length)
		
		//a basic view to see the result
		render('<div style="color:#777777">')
		maze.each { tile ->
			
			if (tile.valid == false){
				render('<div style="background-color:#777777;width:20px;height:20x;border:1px solid #777777;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}else{
				render('<div style="background-color:#FFFFFF;color:#FFFFFF;width:20px;height:20x;border:1px solid #FFFFFF;position:absolute;left:'+tile.x*22+'px;top:'+tile.y*22+'px">x</div>')
			}
				
		}
		render('</div>')
	}
	
	def slowjson(int length) {
		
		if(!length){
			length = 15
		}
		
		def maze = new RandomMazeGenerator().depthFirstMaze(length)
		
		render('<div>')
		render maze as JSON
		render('</div>')
		
	}
}
