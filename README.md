# Pacman
**Pacman Game implementation in Java**
______________________________________


_**Compililation:**_

      javac Pacman.java Gui.java filereader.java Board.java
      
***Execution:***

      java Pacman

**File Game-boards contains three game-boards but you could add new as well just make sure to include them in Pacman.java file**
**by adding lines:**

      GameBoards.add(new filereader("./game-boards/Myboard.txt").get()); 

supposing Myboard.txt is the new game board that you added.

**File gifs contains all the icons if pacman and ghoasts used in the game.**

