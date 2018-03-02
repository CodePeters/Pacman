import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallfont = new Font("Helvetica", Font.BOLD, 14);
    private final Color dotcolor = new Color(255, 251, 253);
    private final Color linecolor = new Color(255, 54, 66);
    private Color mazecolor;
    private boolean ingame = false,dying = false;
    private final int blocksize = 24,nrofblocks = 22,nrofblocksy = 19;
    private final int scrsize = nrofblocks * blocksize;
    private final int scrsizey = nrofblocksy * blocksize;
    private final int pacanimdelay = 2,pacmananimcount = 4;
    private int pacmanspeed ;
    private int pacanimcount = pacanimdelay;
    private int pacanimdir = 1;
    private int pacmananimpos = 0;
    private int pacsleft, score;
    public int pacmanx, pacmany, pacmandx, pacmandy,reqdx, reqdy, viewdx, viewdy;
    private int[] dx, dy,ghostdying,ghostStart,ghostx, ghosty, ghostdx, ghostdy, ghostspeed;
    protected  int[] highscores;
    private Image ghost = new ImageIcon("./gifs/Ghost1.gif").getImage();
    private Image ghost2 = new ImageIcon("./gifs/Ghost2.gif").getImage();
    private Image Scaredghost = new ImageIcon("./gifs/GhostScared1.gif").getImage();
    private Image Scaredghost2 = new ImageIcon("./gifs/GhostScared2.gif").getImage();
    private Image pacman1 = new ImageIcon("./gifs/./gifs/PMO.gif").getImage();
    private Image pacman2up = new ImageIcon("./gifs/PMup1.gif").getImage(),pacman3up = new ImageIcon("./gifs/PMup2.gif").getImage(),pacman4up = new ImageIcon("./gifs/PMup3.gif").getImage();
    private Image pacman2down = new ImageIcon("./gifs/PMdown1.gif").getImage(),pacman3down = new ImageIcon("./gifs/PMdown2.gif").getImage(),pacman4down = new ImageIcon("./gifs/PMdown3.gif").getImage();
    private Image pacman2left = new ImageIcon("./gifs/PMleft1.gif").getImage(),pacman3left = new ImageIcon("./gifs/PMleft2.gif").getImage(),pacman4left = new ImageIcon("./gifs/PMleft3.gif").getImage();
    private Image pacman2right = new ImageIcon("./gifs/PMright1.gif").getImage(),pacman3right = new ImageIcon("./gifs/P./gifs/Mright2.gif").getImage(),pacman4right = new ImageIcon("./gifs/PMright3.gif").getImage();
    private short[] screendata,leveldata;
    private Timer timer,timer2;
    private int cookies=0;
    protected  boolean finished;
    private boolean scared = false;
    private int NumberOfBoards,game=1;
    Vector<short[]> gameboard;
    protected String[] highscoresNames;
    protected FileWriter Scores ;
    private int start,startpos;


    public Board(Vector<short[]> gameboard) {

        this.gameboard=gameboard;
        initVariables();
        setBackground(Color.black);
        setDoubleBuffered(true);
    }

    public void setGame(boolean x,int level){
        startpos=0;
        ingame = x;
        if (level != -1) { game = level; leveldata=gameboard.get(game);}
        initGame();
    }
    public boolean getGame() {return ingame;}

    private void initVariables() {

        NumberOfBoards=gameboard.size();
        leveldata=gameboard.get(0);
        screendata = new short[nrofblocks * nrofblocksy];
        mazecolor = new Color(204, 120, 15);
        d = new Dimension(400, 400);

        ghostx = new int[4]; //4 ghosts
        ghostdx = new int[4];
        ghostStart = new int[4];
        ghostdying=new int[4];
        ghosty = new int[4];
        ghostdy = new int[4];
        ghostspeed = new int[4];//4 ghosts
        highscores = new int[5];
        highscoresNames = new String[5];
        try{
            Scores = new FileWriter("HighScores.txt");
        }catch (IOException e){}

        dx = new int[4];
        dy = new int[4];
        for(int j = 0; j < nrofblocks * nrofblocksy; j++) if(leveldata[j]=='.') cookies++;

        timer = new Timer(40, this);
        timer2 = new Timer(7000, this);
        timer.start();
        initGame();
    }

    private void doAnim() {

        pacanimcount--;
        if (pacanimcount <= 0) {
            pacanimcount = pacanimdelay;
            pacmananimpos = pacmananimpos + pacanimdir;
            if (pacmananimpos == (pacmananimcount - 1) || pacmananimpos == 0) {
                pacanimdir = -pacanimdir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {

        if (dying) death();
        else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze(g2d);
        }
    }

    private void drawScore(Graphics2D g) {

        int i;
        g.setFont(smallfont);
        g.setColor(new Color(96, 128, 255));
        g.drawString( "Score: "+score, scrsizey / 2 + 96,  16);
        int highest= Math.max(score,highscores[4]);
        g.drawString(" highscore: "+highest, 16, 16);
        for (i = 0; i < pacsleft; i++)  g.drawImage(pacman3left, i * 28 + 8, scrsize + 1+24, this);
    }

    private void checkMaze(Graphics2D g) {
        finished = true;
        for(short i=0; i < nrofblocks * nrofblocksy && finished;i++) {
            if (screendata[i] == '.' || screendata[i] == 'O' ) finished = false;
        }

        if (finished) {
            ingame=false;
            timer2.start();
        }
    }

    private void death() {
        pacsleft--;
        if (pacsleft == 0) {ingame = false;timer2.start();}
        startpos=0;
        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        for (int i = 0; i < 4; i++) {
            if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {
                int  pos = ghostx[i] / blocksize + nrofblocksy * (ghosty[i] / blocksize);

                int choice=ThreadLocalRandom.current().nextInt(1,4);
                if(ghostdx[i]>0 && screendata[pos + 1]!='#' && screendata[pos + 19]!='#' && screendata[pos - 19]!='#'
                        && screendata[pos + 1]!='-' && screendata[pos + 19]!='-' && screendata[pos - 19]!='-') {

                    if(choice==1) {ghostdx[i]=1; ghostdy[i]=0;}
                    else if(choice==2) {ghostdx[i]=0; ghostdy[i]=1;}
                    else {ghostdx[i]=0; ghostdy[i]=-1;}
                }
                else if(ghostdx[i]<0 && screendata[pos - 1]!='#' && screendata[pos + 19]!='#' && screendata[pos - 19]!='#'
                        && screendata[pos - 1]!='-' && screendata[pos + 19]!='-' && screendata[pos - 19]!='-') {

                    if(choice==1) {ghostdx[i]=-1; ghostdy[i]=0;}
                    else if(choice==2) {ghostdx[i]=0; ghostdy[i]=1;}
                    else {ghostdx[i]=0; ghostdy[i]=-1;}
                }
                else if(ghostdy[i]>0 && screendata[pos + 19]!='#' && screendata[pos + 1]!='#' && screendata[pos - 1]!='#'
                        && screendata[pos + 19]!='-' && screendata[pos + 1]!='-' && screendata[pos - 1]!='-') {

                    if(choice==1) {ghostdx[i]=1; ghostdy[i]=0;}
                    else if(choice==2) {ghostdx[i]=-1; ghostdy[i]=0;}
                    else {ghostdx[i]=0; ghostdy[i]=1;}
                }
                else if(ghostdy[i]<0 && screendata[pos - 19]!='#' && screendata[pos + 1]!='#' && screendata[pos - 1]!='#'
                        && screendata[pos - 19]!='-' && screendata[pos + 1]!='-' && screendata[pos - 1]!='-') {

                    if(choice==1) {ghostdx[i]=1; ghostdy[i]=0;}
                    else if(choice==2) {ghostdx[i]=-1; ghostdy[i]=0;}
                    else {ghostdx[i]=0; ghostdy[i]=-1;}
                }
                else if( ghostdx[i]>0 && screendata[pos + 1]!='#' && screendata[pos + 1]!='-') ghostdx[i]=1;
                else if(ghostdx[i]>0 && screendata[pos + 1] =='#' ) {
                    ghostdx[i] = 0;
                    if (screendata[pos + 19]!='#' && screendata[pos - 19]!='#')
                        if(ThreadLocalRandom.current().nextInt(1,3)==1) ghostdy[i]=1;
                        else ghostdy[i]=-1;
                    else if(screendata[pos + 19]!='#') ghostdy[i]=1;
                    else if(screendata[pos - 19]!='#') ghostdy[i]=-1;
                    else ghostdy[i]=0;
                }
                else if( ghostdx[i]<0 && screendata[pos - 1]!='#'  && screendata[pos - 1]!='-') ghostdx[i]=-1;
                else if(ghostdx[i]<0 && screendata[pos - 1]=='#' ) {
                    ghostdx[i] = 0;
                    if (screendata[pos + 19]!='#' && screendata[pos - 19]!='#')
                        if(ThreadLocalRandom.current().nextInt(1,3)==1) ghostdy[i]=1;
                        else ghostdy[i]=-1;
                    else if(screendata[pos + 19]!='#') ghostdy[i]=1;
                    else if(screendata[pos - 19]!='#') ghostdy[i]=-1;
                    else ghostdy[i]=0;
                }
                else if( ghostdy[i]>0 && screendata[pos + 19]!='#' && screendata[pos + 19]!='-') ghostdy[i]=1;
                else if( ghostdy[i]>0 && screendata[pos + 19]=='#' ) {
                    ghostdy[i] = 0;
                    if (screendata[pos + 1]!='#' && screendata[pos - 1]!='#')
                        if(ThreadLocalRandom.current().nextInt(1,3)==1) ghostdx[i]=1;
                        else ghostdx[i]=-1;
                    else if(screendata[pos + 1]!='#') ghostdx[i]=1;
                    else if(screendata[pos - 1]!='#') ghostdx[i]=-1;
                    else ghostdx[i]=0;
                }
                else if( ghostdy[i]<0 && screendata[pos - 19]!='#' && screendata[pos - 19]!='-') ghostdy[i]=-1;
                else if( ghostdy[i]<0 && screendata[pos - 19]=='#' ) {
                    ghostdy[i] = 0;
                    if (screendata[pos + 1]!='#' && screendata[pos - 1]!='#')
                        if(ThreadLocalRandom.current().nextInt(1,3)==1) ghostdx[i]=1;
                        else ghostdx[i]=-1;
                    else if(screendata[pos + 1]!='#') ghostdx[i]=1;
                    else if(screendata[pos - 1]!='#') ghostdx[i]=-1;
                    else ghostdx[i]=0;
                }

                if(!scared) {
                    if ((Math.abs(pacmanx - ghostx[i]) / blocksize) <= 3 && pacmany == ghosty[i]) {
                        if (((pacmanx - ghostx[i]) / blocksize == 1) && screendata[pos + 1] != '#') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#') {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        }
                        if (((pacmanx - ghostx[i]) / blocksize == 2) && screendata[pos + 1] != '#' && screendata[pos + 2] != '#'
                                                                     && screendata[pos + 1] != '-' && screendata[pos + 2] != '-') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#' && screendata[pos - 2] != '#'
                                                                             && screendata[pos - 1] != '-' && screendata[pos - 2] != '-') {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        }
                        if (((pacmanx - ghostx[i]) / blocksize == 1) && screendata[pos + 1] != '#' && screendata[pos + 2] != '#' && screendata[pos + 3] != '#'
                                                                     && screendata[pos + 1] != '-' && screendata[pos + 2] != '-' && screendata[pos + 3] != '-') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#' && screendata[pos - 2] != '#' && screendata[pos - 3] != '#'
                                                                             && screendata[pos - 1] != '-' && screendata[pos - 2] != '-' && screendata[pos - 3] != '-') {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        }
                    } else if ((Math.abs(pacmany - ghosty[i]) / blocksize) <= 3 && pacmanx == ghostx[i]) {

                        if (((pacmany - ghosty[i]) / blocksize == 1) && screendata[pos + 19] != '#'  && screendata[pos + 19] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos - 19] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        }
                        if (((pacmany - ghosty[i]) / blocksize == 2) && screendata[pos + 19] != '#' && screendata[pos + 38] != '#'
                                                                     && screendata[pos + 19] != '-' && screendata[pos + 38] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos - 38] != '#'
                                                                             && screendata[pos - 19] != '-' && screendata[pos - 38] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        }
                        if (((pacmany - ghosty[i]) / blocksize == 1) && screendata[pos + 19] != '#' && screendata[pos + 38] != '#' && screendata[pos + 57] != '#'
                                                                     && screendata[pos + 19] != '-' && screendata[pos + 38] != '-' && screendata[pos + 57] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos - 38] != '#' && screendata[pos - 57] != '#'
                                                                             && screendata[pos - 19] != '-' && screendata[pos - 38] != '-' && screendata[pos - 57] != '-') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        }
                    }
                }else{
                    if ((Math.abs(pacmanx - ghostx[i]) / blocksize) <= 3 && pacmany == ghosty[i]) {
                        if (((pacmanx - ghostx[i]) / blocksize == 1) && screendata[pos + 1] != '#' && screendata[pos - 1]!= '#'  ) {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#' && screendata[pos + 1]!= '#') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        }
                        if (((pacmanx - ghostx[i]) / blocksize == 2) && screendata[pos + 1] != '#' && screendata[pos + 2] != '#' && screendata[pos - 1]!= '#') {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#' && screendata[pos - 2] != '#' && screendata[pos + 1]!= '#') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        }
                        if (((pacmanx - ghostx[i]) / blocksize == 1) && screendata[pos + 1] != '#' && screendata[pos + 2] != '#' && screendata[pos + 3] != '#' && screendata[pos - 1]!= '#') {
                            ghostdx[i] = -1;
                            ghostdy[i] = 0;
                        } else if (((pacmanx - ghostx[i]) / blocksize == -1) && screendata[pos - 1] != '#' && screendata[pos - 2] != '#' && screendata[pos - 3] != '#' && screendata[pos + 1]!= '#') {
                            ghostdx[i] = 1;
                            ghostdy[i] = 0;
                        }
                    } else if ((Math.abs(pacmany - ghosty[i]) / blocksize) <= 3 && pacmanx == ghostx[i]) {

                        if (((pacmany - ghosty[i]) / blocksize == 1) && screendata[pos + 19] != '#' && screendata[pos - 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos + 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        }
                        if (((pacmany - ghosty[i]) / blocksize == 2) && screendata[pos + 19] != '#' && screendata[pos + 38] != '#' && screendata[pos - 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos - 38] != '#' && screendata[pos + 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        }
                        if (((pacmany - ghosty[i]) / blocksize == 1) && screendata[pos + 19] != '#' && screendata[pos + 38] != '#' && screendata[pos + 57] != '#' && screendata[pos - 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = -1;
                        } else if (((pacmany - ghosty[i]) / blocksize == -1) && screendata[pos - 19] != '#' && screendata[pos - 38] != '#' && screendata[pos - 57] != '#' && screendata[pos + 19]!= '#') {
                            ghostdx[i] = 0;
                            ghostdy[i] = 1;
                        }
                    }

                }

                int CookieCount=0;
                for(int j = 0; j < nrofblocks * nrofblocksy; j++) if(screendata[j]=='.') CookieCount++ ;

                if( CookieCount < 0.4*cookies) {
                    ghostspeed[i]= 4;
                    if(scared) ghostspeed[i]=3;
                }else {
                    ghostspeed[i] = 3;
                    if(scared) ghostspeed[i]=2;
                }
            }

            ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
            ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);

            if (ghostdying[i]==1) {
                ghostx[i] = (ghostStart[i] % nrofblocksy) * blocksize;
                ghosty[i] = (ghostStart[i] / nrofblocksy) * blocksize;
            }

            if (!scared) {
                if (ghostdx[i] >= 0) g2d.drawImage(ghost, ghostx[i] + 1, ghosty[i] + 1 + 24, this);
                else g2d.drawImage(ghost2, ghostx[i] + 1, ghosty[i] + 1 + 24, this);
            }
            else{
                if (ghostdx[i] >= 0) g2d.drawImage(Scaredghost, ghostx[i] + 1, ghosty[i] + 1 + 24, this);
                else g2d.drawImage(Scaredghost2, ghostx[i] + 1, ghosty[i] + 1 + 24, this);
            }

            if (!scared && pacmanx > (ghostx[i] - 12) && pacmanx < (ghostx[i] + 12) && pacmany > (ghosty[i] - 12) && pacmany < (ghosty[i] + 12) && ingame) dying = true;
            else if (scared && pacmanx > (ghostx[i] - 12) && pacmanx < (ghostx[i] + 12) && pacmany > (ghosty[i] - 12) && pacmany < (ghosty[i] + 12) && ingame) {
                int localcount=0;
                ghostdying[i]=1;
                for (int k=0;k<4;k++) if (ghostdying[k] ==1) localcount++;
                if(localcount==1) score+=200;
                else if (localcount==2) score+=400;
                else if (localcount==3) score+=800;
                else if (localcount==4) score+=1600;

            }
        }
    }

    private void movePacman() {

        int pos;
        short ch;
        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx;
            pacmandy = reqdy;
            viewdx = pacmandx;
            viewdy = pacmandy;
        }

        if (pacmanx % blocksize == 0 && pacmany % blocksize == 0) {
            pos = pacmanx / blocksize + nrofblocksy * (pacmany / blocksize);
            ch = screendata[pos];

            if (ch  == '.') {
                screendata[pos] = ' ';
                score+=10;
            }

            if (ch  == 'O') {
                screendata[pos] = ' ';
                timer2.start();
                for(int i=0;i<4;i++) {ghostdx[i]=-ghostdx[i]; ghostdy[i]=-ghostdy[i];}
                scared=true;
                score+=50;
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!((reqdx == -1 && reqdy == 0 && pos%19>0 && screendata[pos-1]== '#') || (reqdx == 1 && reqdy == 0 && pos%19<18 && screendata[pos+1]== '#')
                        || (reqdx == 0 && reqdy == -1 && pos/19<22 && screendata[pos-19]== '#')
                        || (reqdx == 0 && reqdy == 1 &&  pos/19>0 && screendata[pos+19]== '#'))) {
                    viewdx = pacmandx = reqdx;
                    viewdy = pacmandy = reqdy;
                }
            }
            // Check for standstill
            if ((pacmandx == -1 && pacmandy == 0 && pos%19>0 && screendata[pos-1]== '#')
                    || (pacmandx == 1 && pacmandy == 0 && pos%19<18 && screendata[pos+1]== '#')
                    || (pacmandx == 0 && pacmandy == -1 && pos/19<22 && screendata[pos-19]== '#')
                    || (pacmandx == 0 && pacmandy == 1 && pos/19>0 && screendata[pos+19]== '#')) {
                pacmandx = 0;
                pacmandy = 0;
            }

            int CookieCount=0;
            for(int j = 0; j < nrofblocks * nrofblocksy; j++) if(screendata[j]=='.') CookieCount++ ;

            if( CookieCount < 0.4*cookies) {
                pacmanspeed = 4;
                if(scared) pacmanspeed=8;
            }else {
                pacmanspeed = 3;
                if(scared) pacmanspeed=6;
            }
        }

        pacmanx +=  pacmanspeed * pacmandx;
        pacmany +=  pacmanspeed * pacmandy;

    }

    private void drawPacman(Graphics2D g2d) {
         if (viewdx == -1) drawPacman2(g2d,"Left");
         else if (viewdx == 1) drawPacman2(g2d,"Right");
         else if (viewdy == -1) drawPacman2(g2d,"Up");
         else drawPacman2(g2d,"Down");
    }

    private void drawPacman2(Graphics2D g2d,String s) {

        Image newimage2,newimage3,newimage4;
        if(s.equals("Left")) { newimage2=pacman2left; newimage3=pacman3left; newimage4=pacman4left; }
        else if(s.equals("Right")) { newimage2=pacman2right; newimage3=pacman3right; newimage4=pacman4right; }
        else if(s.equals("Up")) { newimage2=pacman2up; newimage3=pacman3up; newimage4=pacman4up; }
        else { newimage2=pacman2down; newimage3=pacman3down; newimage4=pacman4down; }

        switch (pacmananimpos) {
            case 1:
                g2d.drawImage(newimage2, pacmanx + 1, pacmany + 1+24, this);
                break;
            case 2:
                g2d.drawImage(newimage3, pacmanx + 1, pacmany + 1+24, this);
                break;
            case 3:
                g2d.drawImage(newimage4, pacmanx + 1, pacmany + 1+24, this);
                break;
            default:
                g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1+24, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        for (int y = 0; y < scrsize; y += blocksize) {
            for (int x = 0; x < scrsizey; x += blocksize) {
                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));
                if (screendata[i] == '#')  g2d.fillRect(x , y+24 , blocksize - 1,  blocksize - 1);
                else if (screendata[i] == '-') { g2d.setColor(linecolor); g2d.drawLine(x , y+(blocksize - 1)/2+24 , x+blocksize - 1,  y+(blocksize - 1)/2+24); }
                else if (screendata[i] == '.') { g2d.setColor(dotcolor); g2d.fillRect(x + 11, y + 11+24, 2, 2); }
                else if (screendata[i] == 'O') { g2d.setColor(dotcolor); g2d.fillOval(x+2,y+2+24,20,20);}
                i++;
            }
        }
        if(!ingame) {
            for(int j=0;j< 4;j++){ g2d.drawImage(ghost,  ghostx[j] + 1, ghosty[j] + 1 + 24, this); }
            if(pacsleft==0) showMessageScreen(g2d,"Game Over !!");
            if(finished) {
                showMessageScreen(g2d,"You Won !!");
                drawPacman(g2d);
            }

        }

    }

    private void initGame() {
        scared = false;
        pacsleft = 3;
        score = 0;
        if(timer2.isRunning())
            timer2.getActionListeners()[0].actionPerformed(new ActionEvent(timer2, ActionEvent.ACTION_PERFORMED, null) {});

        for (int i = 0; i < nrofblocks * nrofblocksy; i++) screendata[i] = leveldata[i]; //init level
        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        for(int j = 0; j < nrofblocks * nrofblocksy; j++) screendata[j]=leveldata[j];
        for (i = 0; i < 4; i++) {
            for (int j = 0; j < nrofblocks * nrofblocksy; j++) {
                if (screendata[j] == 'F') {
                    ghostStart[i]=j;
                    ghostx[i] = (j % nrofblocksy) * blocksize;
                    ghosty[i] = (j / nrofblocksy) * blocksize;
                    screendata[j] = ' ';
                    break;
                }
                if(screendata[j]=='-') {
                    if (screendata[j - 1] == '#' && screendata[j + 38] == '#') start = j - 19;
                    else if (screendata[j - 1] == '#' && screendata[j - 38] == '#') start = j + 19;
                    else if (screendata[j - 19] == '#' && screendata[j + 2] == '#') start = j - 1;
                    else start = j + 1;
                }
            }

            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;
            ghostspeed[i]=2;
        }

        for (i = 0; i < nrofblocks*nrofblocksy; i++)
            if (leveldata[i] == 'P'){
                         pacmanx = (i % nrofblocksy) * blocksize;
                         pacmany = (i / nrofblocksy) * blocksize;
            }
        pacmandx = pacmandy = 0;
        reqdx = reqdy = 0;
        viewdx = -1;
        viewdy = 0;
        dying = false;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();
        if (ingame) {
            if (startpos == 0) {
                for (int i = 0; i < 4; i++) {
                    ghostx[i] = (start % nrofblocksy) * blocksize;
                    ghosty[i] = (start / nrofblocksy) * blocksize;
                }
                startpos = 1;
            }
            playGame(g2d);
        }
        if(!timer2.isRunning()) {
            if (pacsleft == 0 || (finished == true && game >= NumberOfBoards))
                showMessageScreen(g2d, "Press 'E' for EXIT or 'S' for 'Start'");

        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Object Source=e.getSource();
        if(Source==timer2) {
            if(!finished && !(pacsleft==0)){scared=false;}
            timer2.stop();
            for(int i=0;i< 4;i++) {
                if(ghostdying[i]==1) {
                    ghostx[i] = (start % nrofblocksy) * blocksize;
                    ghosty[i] = (start / nrofblocksy) * blocksize;
                    ghostdx[i]=1;
                }
                ghostdying[i]=0;
            }
            if(pacsleft>0 && finished){
                if(game<NumberOfBoards){

                    if(score>highscores[0]){
                        String s=JOptionPane.showInputDialog("please enter your name");
                        highscores[0]=score;
                        highscoresNames[0]=s;
                        mysort(highscores,highscoresNames);
                        try{
                            for(int j=4;j>=0;j--) Scores.write(highscoresNames[j]+" "+highscores[j]+"\n");
                            Scores.flush();
                        }
                        catch (IOException e1){}
                    }

                    leveldata=gameboard.get(game);
                    game++;
                    finished=false;
                    startpos=0;
                    if(game<= NumberOfBoards) initGame();
                }
                else {
                         if(score>highscores[0]){
                             String s=JOptionPane.showInputDialog("please enter your name");
                             highscores[0]=score;
                             highscoresNames[0]=s;
                             mysort(highscores,highscoresNames);
                             try{
                                 for(int j=4;j>=0;j--) Scores.write(highscoresNames[j]+" "+highscores[j]+"\n");
                                 Scores.flush();
                             }
                             catch (IOException e1){}
                         }
                }
            }
        }

        repaint();
    }

    private void mysort(int[] array,String[] array2) {

        int swap;
        for (int c = 0; c < 4; c++) {
            for (int d = 0; d < 4 - c ; d++) {
                if (array[d] > array[d + 1]) {
                    swap = array[d];
                    array[d] = array[d + 1];
                    array[d + 1] = swap;
                    String s=array2[d];
                    array2[d] = array2[d + 1];
                    array2[d + 1] = s;
                }
            }
        }
    }

    private void showMessageScreen(Graphics2D g2d,String s) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, scrsize / 2 - 30, scrsize - 170, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, scrsize / 2 - 30, scrsize - 170, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (scrsize - metr.stringWidth(s)) / 2- 20, scrsize / 2);
    }




}
