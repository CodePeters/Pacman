import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;


public class GUI extends JFrame  implements ActionListener,KeyListener{

    private JButton st= new JButton("START");
    private Board brd;
    private JMenuItem eMenuItem = new JMenuItem("Exit");
    private JMenuItem eMenuItem2 = new JMenuItem("Start");
    private JMenuItem eMenuItem4 = new JMenuItem("Load");
    private JMenuItem eMenuItem3 = new JMenuItem("High Scores");
    private JMenuBar menuBar;
    private JMenu menu;

    GUI(Vector<short[]> gameboard)
    {
        setTitle("MediaLab Pac-Man");
        brd=new Board(gameboard);


        JPanel panedown=new JPanel();
        st.addActionListener(this);
        panedown.setBackground(Color.black);
        panedown.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panedown.add(st);


        menuBar = new JMenuBar();
        menuBar.setBackground(Color.black);

        menu= new JMenu("GAME");
        menu.setMnemonic(KeyEvent.VK_F);

        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.addActionListener(this);
        eMenuItem2.addActionListener(this);
        eMenuItem3.addActionListener(this);
        eMenuItem4.addActionListener(this);

        menu.add(eMenuItem);
        menu.add(eMenuItem2);
        menu.add(eMenuItem3);
        menu.add(eMenuItem4);
        menuBar.add(menu);
        setJMenuBar(menuBar);


        addKeyListener(this);
        setFocusable(true);

        add(panedown,BorderLayout.PAGE_END);
        add(brd,BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(460, 680));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){

        Object source = e.getSource();
        if(source==st || source==eMenuItem2) brd.setGame(true,-1);
        else if(source==eMenuItem){
            try {
                brd.Scores.flush();
                brd.Scores.close();
            }catch (IOException e1){}
            dispose();
            System.exit(0);
        }
        else if(source == eMenuItem3){
            String output ="";
            for(int i=4; i>=0; i--) output += brd.highscoresNames[i] + " " + brd.highscores[i] + "\n";
            JOptionPane.showMessageDialog(null,output);
        }else if(source == eMenuItem4){
            String s=JOptionPane.showInputDialog("What level you want to play 1,2 or 3 ??");
            brd.setGame(false,Integer.parseInt(s)-1);
        }
        requestFocusInWindow();
    }

    public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (brd.getGame()) {
                if (key == KeyEvent.VK_LEFT) { brd.reqdx = -1; brd.reqdy = 0; }
                else if (key == KeyEvent.VK_RIGHT) { brd.reqdx = 1; brd.reqdy = 0; }
                else if (key == KeyEvent.VK_UP) { brd.reqdx = 0; brd.reqdy = -1; }
                else if (key == KeyEvent.VK_DOWN) {brd.reqdx = 0; brd.reqdy = 1; }
            }
            else {
                if (key == 'S' || key == 's' && brd.finished==true ) brd.setGame(true,-1);
                else  if (key == 'E' || key== 'e' && brd.finished==true) {
                    try {
                        brd.Scores.flush();
                        brd.Scores.close();
                    }catch (IOException e1){}
                    dispose();
                    System.exit(0); }
            }

    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == Event.LEFT || key == Event.RIGHT
                || key == Event.UP || key == Event.DOWN) {
            brd.reqdx = 0;
            brd.reqdy = 0;
        }
    }
    public void keyTyped(KeyEvent k) {}
}
