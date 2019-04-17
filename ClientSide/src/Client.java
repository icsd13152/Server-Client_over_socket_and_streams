
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Client extends JFrame {

    private JPanel row1, row2, row3;
    private JLabel n, fname, size, fpath;
    private JButton send, choose;
    private Socket sock = null;
    private File file = null;

    public Client() {
        super("Hello!");
        row1 = new JPanel();
        row2 = new JPanel();
        row3 = new JPanel();

        setSize(700, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        n = new JLabel("Choose your file to send: ");
        fname = new JLabel(" ");
        size = new JLabel(" ");
        fpath = new JLabel(" ");
        fname.setBounds(50, 50, 100, 30);
        send = new JButton("Send");
        choose = new JButton("Choose");

        Container pane = getContentPane();
        try {
            sock = new Socket("localhost", 8080);//dhmiourgia sundeshs me socket
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        GridLayout glayout = new GridLayout(2, 0);
        pane.setLayout(glayout);
        FlowLayout layout = new FlowLayout();
        row1.setLayout(layout);
        row1.add(n);
        row1.add(choose);

        pane.add(row1);
        row2.setLayout(layout);
        row2.add(fname);

        pane.add(row2);
        row3.setLayout(layout);
        row3.add(send);
        pane.add(row3);

        choose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                FileInputStream fis = null;
                BufferedWriter bw = null;

                try {

                    JFileChooser chooser = new JFileChooser();
                    String fileName = null;
                    int returnVal = chooser.showOpenDialog(pane);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = chooser.getSelectedFile();
                        fileName = file.getName();
                        fname.setText(fileName);

                        InetAddress ad = InetAddress.getLocalHost();//pernw thn topikh ip
                        String ip = ad.getHostAddress();
                        bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));//stelnw mesw stream san mhnuma ston server to onoma tou arxeiou

                        bw.write(fileName);
                        bw.newLine();//newline gia na diavazei seira seira. einai aparaitito
                        bw.flush();//adeiasma
                        bw.write(ip);
                        bw.newLine();//newline gia na diavazei seira seira. einai aparaitito
                        bw.flush();
                    }

                } catch (IOException ex) {
                    System.out.println("can't connect to server!");
                }

            }

        });
        send.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                //dhleseis twn streams
                DataInputStream in = null;

                OutputStream out = null;

                FileInputStream fis = null;
                BufferedInputStream bis = null;
                DataOutputStream dos = null;
                BufferedReader bfr = null;

                try {

                    File f = new File(file.getAbsolutePath());//dhmiourgia file me path arxeiou 

                    int x = (int) file.length();//pernw megethos arxeiou kanontas casting se int epeidh to megethos to pernei se long
                    byte[] mybytearray = new byte[x];//pinakas apo bytes. to megethos tou einai analoga me to megethos tou arxeiou

                    fis = new FileInputStream(f);
                    bis = new BufferedInputStream(fis);
                    in = new DataInputStream(bis);

                    out = sock.getOutputStream();//deinw eksodo mesw stream
                    dos = new DataOutputStream(out);

                    int len;
                    while ((len = in.read(mybytearray, 0, mybytearray.length)) > -1) {//kanei epanalhpseis grafontas to arxeio ston server mexri na teleiwsoun ta bytes pou diavase
                        dos.writeLong(mybytearray.length);//stelnw megethos arxeiou
                        dos.flush();

                        dos.write(mybytearray, 0, mybytearray.length);//grapsimo dedomenwn se bytes ston server
                        dos.flush();//adeiasma buffer
                    }
                    String answer;
                    bfr = new BufferedReader(new InputStreamReader(sock.getInputStream()));//stream gia diavasma
                    answer = bfr.readLine();//diavasma mhnumatos gia to ean estalh to arxeio
                    if (answer.equals("completed")) {
                        JOptionPane.showMessageDialog(pane, "the file send succesfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(1); //telos tou build
                    } else if (answer.equals("not-completed")) {
                        JOptionPane.showMessageDialog(pane, "the file has not sended", "Message", JOptionPane.INFORMATION_MESSAGE);
                        //System.exit(1); //telos tou build
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        //kleisimo streams
                        dos.close();
                        in.close();
                        bis.close();
                        out.close();
                        fis.close();

                        sock.close();//kleisimo socket
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        this.addWindowListener(
                new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent
            ) {
                int Answer = JOptionPane.showConfirmDialog(null, "Are you sure to close this window?", "Sure for Exit ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (Answer == JOptionPane.YES_OPTION) {

                    System.exit(1);
                } else {
                    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                }
            }
        }
        );
        setContentPane(pane);

        pack();

    }
}
