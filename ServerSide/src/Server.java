
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.DataInputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public Server() {
        //streams
        Socket sock = null;
        BufferedReader br = null;

        DataInputStream input = null;
        OutputStream dos = null;
        String fileName;
        InputStream is = null;
        BufferedWriter bw = null;

        int bufferSize;

        try {

            ServerSocket ss = new ServerSocket(8080);//dhmiourgia socket me port
            System.out.println("Waiting for a Client...");
            sock = ss.accept();//apodoxh xrhsth

            System.out.println("Client connected!");

            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            fileName = br.readLine();//diavazw to mhnuma pou esteile o client
            String ip = br.readLine();

            System.out.println("User with ip " + ip + " is sending a file with name " + fileName);

            int bytesRead;

            is = sock.getInputStream();//pernw ap oto socket mesw stream auto pou stenei o client
            input = new DataInputStream(is);
            dos = new FileOutputStream(fileName);
            long size = input.readLong();//diavasma megethous pou mou stelnei o client tupou long
            System.out.println("size: " + size);
            byte[] buffer = new byte[(int) size];
            //epanalhpsh oso to megethos tou arxeiou einai megalutero tou 0 kai mexri na metaferei ola ta bytes pou diavazei 
            while (size > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                System.out.println("recieving file...");
                dos.write(buffer, 0, bytesRead);//grapsimo twn dedomenwn
                dos.flush();
                size -= bytesRead;//meiwsh megethous
            }
            System.out.println("completed!");
            bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            File file = new File(fileName);//dhmiourgia arxeiou gia na mporesw na tsekarw ean to metefere
            if (file.exists()) {//ean uparxei to arxeiou pou esteile o client (dhladh ean to esteile) tote stelnei analogo mhnuma ston client
                bw.write("completed");//grafei to mhnuma
                bw.newLine();//nea grammh
                bw.flush();//apeleutherwsh buffer
            } else {
                bw.write("not-completed");
                bw.newLine();
                bw.flush();
            }

        } catch (IOException ex) {
            System.out.println("Error! Client not connected!");
        } finally {
            try {
                //kleisimo twn streams
                br.close();
                input.close();
                is.close();
                dos.close();
                bw.close();
                sock.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
