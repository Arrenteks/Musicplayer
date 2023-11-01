package de.medieninformatik.prog4;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //IAudioPlayer currentPlayer = new SimpleAudioPlayer();



        List<String> songList = new ArrayList<>();
        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(Paths.get("C:\\Users\\Daniel\\Desktop\\Musik - wav"));
            for (Path path: directoryStream) {
               songList.add(path.toString());
            }
            directoryStream.close();
        } catch (IOException e) {
            System.err.println("Der angegebene Pfad konnte nicht gefunden werden");
            e.printStackTrace();
        }

        try{
            IAudioPlayer simpleAudioPlayer = new SimpleAudioPlayer(songList);

            simpleAudioPlayer.play();
            Scanner sc = new Scanner(System.in);

            while(true){
                System.out.println("1. pause");
                System.out.println("2. resume");
                System.out.println("3. restart");
                System.out.println("4. stop");
                System.out.println("5. jump to time");
                System.out.println("6. skip the current song");
                System.out.println("7. revisit the previous song");
                System.out.println("8. Pick a random song");
                int input = sc.nextInt();
                simpleAudioPlayer.goToChoice(input);
                if(input == 4) break;
            }
            sc.close();

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }
}
