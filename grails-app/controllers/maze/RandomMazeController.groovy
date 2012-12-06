package maze

import grails.converters.JSON

import java.awt.Cursor;
import java.util.Random

class RandomMazeController {
	
	List tiles = [] //ordered list of tiles
	List visited = []

    def index(int length) {
		
		//generate a maze
		//int length = 15	
		def maze = depthFirstMaze2(length)	
	
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
			
		def maze = depthFirstMaze2(length)	
	
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
		
		def maze = depthFirstMaze2(length)
		
		render('<div>')
		render maze as JSON
		render('</div>')
		
	}
	
	def slowmaze(int length) {
		
		//generate a maze
		if(!length){
			length = 15
		}
			
		def maze = depthFirstMazeSlow(length)
	
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
		
		def maze = depthFirstMaze(length)
		
		render('<div>')
		render maze as JSON
		render('</div>')
		
	}
	
	def depthFirstMazeSlow(int len){
		
		//based on "Depth First" @ http://en.wikipedia.org/wiki/Maze_generation_algorithm
		
		//list of tiles
		//set of visited tiles
		def x = 1..len
		def y = 1..len
		
	//	Set options =[] //places to tunnel to next
		
		
		//initialise tiles set and add border tiles to visited
		y.each {yCor ->
			x.each{xCor ->
				Tile t = new Tile()
				t.x = xCor
				t.y = yCor
				t.valid = false //set all tiles to false - ie 'wall'
				
				tiles.add(t)
			}
			
		}
	
		int randomX = 2
		int randomY = 2
		int index = tiles.findIndexOf {it.x == randomX &&
										it.y == randomY }
		
		//set the start tile to valid (path not wall)
		tiles[index].valid = true;
		

		visited.add(tiles[index])
		
		//create a random valid pathway
		snake2(randomX,randomY,index,len)
		
		for(int i=0; i<2; i++){
		
			//return up the path to check for new paths
			//iterate path tiles, check for options
			def viewVisited = visited.collect()
	
			viewVisited.each{ cursor ->
				//is there a better way to get the index of cursor?
			//	int ind = viewVisited.findIndexOf {it.x == cursor.x &&
			//									   it.y == cursor.y }
				snake(cursor.x,cursor.y, len)
			}
			
		}
		return tiles		
	}
	
	def depthFirstMaze2(int len){
		
		//based on "Depth First" @ http://en.wikipedia.org/wiki/Maze_generation_algorithm
		
		//list of tiles
		//set of visited tiles
		def x = 1..len
		def y = 1..len
		
	//	Set options =[] //places to tunnel to next
		
		
		//initialise tiles set and add border tiles to visited
		y.each {yCor ->
			x.each{xCor ->
				Tile t = new Tile()
				t.x = xCor
				t.y = yCor
				t.valid = false //set all tiles to false - ie 'wall'
				
				tiles.add(t)
				
				//is tile on the border?
				/*
				if(xCor==1||xCor==len||yCor==1||yCor==len){
					visited.add(t)
				}
				*/
			}
			
		}
		
		//pick a random tile to start from
		Random random = new Random()
/*
		//get random x,y but dont want a zero or 1
		int randomX = random.nextInt(len-2) 
		int randomY = random.nextInt(len-2)
		randomX += 2 //in case it is zero
		randomY += 2
		//start position should be odd co-ord if length is even
		if(len % 1){ //len is even
			randomX+=1
			randomY+=1
		}
		
		//start at a random tile, add start tile to visited set
		Tile cursorb = tiles.find { it.x == randomX &&
								   it.y == randomY } 
		
		//find the index of the random tile
		int index = tiles.findIndexOf {it.x == randomX &&
									   it.y == randomY }
*/
		
		int randomX = 2
		int randomY = 2
		int index = tiles.findIndexOf {it.x == randomX &&
										it.y == randomY }
		
		//set the start tile to valid (path not wall)
		tiles[index].valid = true;
		

		visited.add(tiles[index])
		
		//create a random valid pathway
		snake2(randomX,randomY,index,len)
		
		
		//snake(2,12,len)
		int end = (((len-1)/2) * ((len-1)/2))-2
		int lastSizore = visited.size()
		boolean search = true
		
		for(int i=0; i<6; i++){
		
			//return up the path to check for new paths
			//iterate path tiles, check for options
			def viewVisited = visited.collect()
	
			viewVisited.each{ cursor ->
				//is there a better way to get the index of cursor?
				int ind = viewVisited.findIndexOf {it.x == cursor.x &&
												   it.y == cursor.y }
				snake2(cursor.x,cursor.y, ind, len)
			}
			
		}	
		
	//	}
	
/*			
			//check adjacent tiles (check 2 spaces to account for wall)
			//check move right 2
			int rightTile = tiles.findIndexOf {it.x == cursor.x +2 &&
											   it.y == cursor.y  }
			if(checkValidTile(rightTile,len) == true && rightTile > 0){
				options.add(rightTile)
			}
			//check move left 2
			int leftTile = tiles.findIndexOf {it.x == cursor.x -2 &&
											  it.y == cursor.y  }
			if(checkValidTile(leftTile,len) == true && leftTile > 0 ){
				options.add(leftTile)
			}
			//check move up 2
			int upTile = tiles.findIndexOf {it.x == cursor.x &&
											it.y == cursor.y + 2 }
			if(checkValidTile(upTile,len) == true && upTile > 0){
				options.add(upTile)
			}
			//check move down 2
			int downTile = tiles.findIndexOf {it.x == cursor.x &&
											  it.y == cursor.y -2 }
			if(checkValidTile(downTile,len) == true && downTile > 0){
				options.add(downTile)
			}
			
			//pick a random tile from the options, if any
			if(options){
				//int randomIndex = options.find {it}
				//tiles[randomIndex].valid = true;
			
					snake(cursor.x,cursor.y, len)
		
			}
		}
*/
	
		return tiles
		
	}
	

		//select a random neighbouring cell that has not yet been visited
		
		//move to cell (add the new tile to the visited set)
		
		//repeat select random cell....
 

	void snake(int newX, int newY, int len){
		
		int preX, preY
		int newIndex
		int randomDir
		
		char ignore
		int cursorX = newX
		int cursorY = newY
		Random random = new Random()
		
	//	for(int i=0;i<100;i++) {
		tiles.each{
			 randomDir = random.nextInt(4)
			
			if(randomDir==3 && ignore!="r"){//right
				newX = cursorX + 2
				newY = cursorY 
				preX=newX - 1
				preY=newY
				//dont move back to visited
				ignore = "l"
				
			}else if(randomDir==2 && ignore!="l"){//left
				newX = cursorX - 2
				newY = cursorY
				preX=newX + 1
				preY=newY
				ignore = "r"
			}else if(randomDir==1 && ignore!="u"){//up ***
				newY = cursorY + 2
				newX= cursorX
				preY=newY-1
				preX=newX
				ignore = "d"
			}else if(randomDir==0 && ignore!="d"){//down
				newY = cursorY - 2
				newX= cursorX
				preY=newY+1
				preX=newX
				ignore = "u"
			}
			
			//check the tile at the new co-ordinates
			newIndex = tiles.findIndexOf {it.x == newX &&
										  it.y == newY }
			
			if(checkValidTile(newIndex,len) == true ){
				
				//move the cursor to this tile and set to valid
				tiles[newIndex].valid = true;
				cursorX= newX
				cursorY=newY
				visited.add(tiles[newIndex])
				//break down the wall between the cursor and the last tile
				int preIndex = tiles.findIndexOf {it.x == preX &&
												  it.y == preY }
				tiles[preIndex].valid = true;
			}

		}
	}
	
	List getOptions(int index, int len){
		
		List result = []
		//check adjacent tiles (check 2 spaces to account for wall)
		//check move right 2
		int rightTile = tiles.findIndexOf {it.x == tiles[index].x +2 &&
										it.y == tiles[index].y  }
		if(checkValidTile(rightTile,len) == true && rightTile > 0){
			result.add("r")
		}
		//check move left 2
		int leftTile = tiles.findIndexOf {it.x == tiles[index].x -2 &&
										it.y == tiles[index].y  }
		if(checkValidTile(leftTile,len) == true && leftTile > 0 ){
			result.add("l")
		}
		//check move up 2
		int upTile = tiles.findIndexOf {it.x == tiles[index].x &&
										it.y == tiles[index].y + 2 }
		if(checkValidTile(upTile,len) == true && upTile > 0){
			result.add("u")
		}
		//check move down 2
		int downTile = tiles.findIndexOf {it.x == tiles[index].x &&
										  it.y == tiles[index].y -2 }
		if(checkValidTile(downTile,len) == true && downTile > 0){
			result.add("d")
		}
		
		return result
	}
	
	void snake2(int newX, int newY, int index, int len){
		//make a snake through the tiles that ends where no further moves are possible
		
		int preX, preY
		int newIndex
		//int randomDir
		
		//get initial values for x and y
		
		char ignore
		int cursorX = newX
		int cursorY = newY
		Random random = new Random()
		
		//get options set
		List options = getOptions(index, len)
		
		//iterate while options not empty
		while (options){
		
		//search for new options
		//repeat until no options
		
//	for(int i=0;i<100;i++) {
		//tiles.each{
			//pick a random direction, go there options[r]
			int r = random.nextInt(options.size())
			//pick an option, move there
			char randomDir = options[r]
			if(randomDir=="r" && ignore!="r"){//right
				newX = cursorX + 2
				newY = cursorY
				preX=newX - 1
				preY=newY
				//dont move back to visited
				ignore = "l"
				
			}else if(randomDir=="l" && ignore!="l"){//left
				newX = cursorX - 2
				newY = cursorY
				preX=newX + 1
				preY=newY
				ignore = "r"
			}else if(randomDir=="u" && ignore!="u"){//up ***
				newY = cursorY + 2
				newX= cursorX
				preY=newY-1
				preX=newX
				ignore = "d"
			}else if(randomDir=="d" && ignore!="d"){//down
				newY = cursorY - 2
				newX= cursorX
				preY=newY+1
				preX=newX
				ignore = "u"
			}
			
			//check the tile at the new co-ordinates
			newIndex = tiles.findIndexOf {it.x == newX &&
										  it.y == newY }
			
			if(checkValidTile(newIndex,len) == true ){ //might not need this, already been checked, remove for performance if necessary
				
				//move the cursor to this tile and set to valid
				tiles[newIndex].valid = true;
				cursorX= newX
				cursorY=newY
				visited.add(tiles[newIndex])
				//break down the wall between the cursor and the last tile
				int preIndex = tiles.findIndexOf {it.x == preX &&
												  it.y == preY }
				tiles[preIndex].valid = true;
			//add
			//visited.add(tiles[preIndex])
				options = getOptions(newIndex, len)
		}else{
				options.remove(r)
				
			}
			
//tiles.each
		}
	}
	
	boolean checkValidTile(int index, int len){
		boolean result;
		
		if(index > 0){
			
			//if(!visited[index]){
			if(tiles[index].valid == false){ //has the tile been visited before?
				//tile not visited, is it out of bounds?
				if(tiles[index].x > len|| tiles[index].y > len || tiles[index].x < 2 || tiles[index].y < 2 ){
				
					return false //tile is out of bounds
				}else{
					return true //tile is unvisited and within bounds
				}
						
			}else{
				return false //tile has been visited
			}
		}else{
			return false //the tile does not exist
		}
	}

	

	
	def randomMaze(int len) {
		
		def x = 1..len
		def y = 1..len
		
		List tiles = []
		
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
				
				//make the first corner start form x0,y0 not x1,y1
				t.x--
				t.y--
				tiles.add(t)
			}
		}
		return tiles
	}
}
