import java.io.*;
import java.util.Vector;

public class Pacman {
    public static void main(String args[]) throws IOException {

        int NumberOfGameBoards=3;
        Vector <short []> GameBoards=new Vector<>(NumberOfGameBoards);
        GameBoards.add(new filereader("./game-boards/game1.txt").get());
        GameBoards.add(new filereader("./game-boards/game2.txt").get());
        GameBoards.add(new filereader("./game-boards/game3.txt").get());
        new GUI(GameBoards);
    }
}
