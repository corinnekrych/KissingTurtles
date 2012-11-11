package maze

import grails.converters.JSON
import java.util.Random



class RandomMazeController {

    def index() {
		
		//generate a maze
		int length = 15		
		def maze = randomMaze(length)
		
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
	
	def json() {	
		
		//generate a maze
		int length = 15
		def maze = randomMaze(length)
		
		render('<div>')
		render maze as JSON
		render('</div>')
		
	}
	
	def randomMaze(int len) {
		
		def x = 1..len
		def y = 1..len
		
		Set tiles = []
		
		y.each {yCor ->
			x.each{xCor ->
				Tile t = new Tile()
				t.x = xCor
				t.y = yCor
				
				Random random = new Random()
				int randomNo = random.nextInt(4)
				//crate a maze by making some tiles valid
				
				
				//the border
				if(xCor==1||xCor==len||yCor==1||yCor==len){
					t.valid = false
				}else{
					t.valid = true
				}
				
				
				//some middle tiles			
				
				if (xCor % 2){ //x is even
					if(yCor % 2){ //y is even, x is even
						t.valid = false
					}else{//y is odd, x is even
						
						if(randomNo==0) t.valid = false
					}
				}else{ //x is odd
					if(yCor % 2){ //y is even, x is odd
						if(randomNo==1) t.valid = false
					}else{//y is odd, x is odd
					
					}
				}
				
				
				//entry gaps for the turtles
				if(yCor>3&&yCor<7){
					if (xCor==1) t.valid = true
				}
				
				if(yCor<len-2&&yCor>len-6){
					if (xCor==len) t.valid = true
				}

				
				tiles.add(t)
			}
		}
		return tiles
	}
}
