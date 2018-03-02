import java.io.*;

public class filereader {

        private  short [] gameboard;

        filereader(String FileName) throws IOException{

            int i,r,N = 22*19;
            gameboard=new short[N];

            InputStream in = new FileInputStream(FileName);
            Reader reader = new InputStreamReader(in);
            Reader buffer = new BufferedReader(reader);

            i=0;
            while ((r = buffer.read()) != -1) {
                gameboard[i]=(short)r;
                buffer.read();
                i++;
             }

        }

    protected short[] get() {
        return gameboard;
    }
}
