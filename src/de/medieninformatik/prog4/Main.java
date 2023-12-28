package de.medieninformatik.prog4;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<String> songList = new ArrayList<>();// initialize empty list
        Scanner sc = new Scanner(System.in);//remove when UI is done

        Thread printingHook = new Thread(()->System.out.println("Shutting down"));// adding serializable functionality for getting previous music
        Runtime.getRuntime().addShutdownHook(printingHook);//what should be done, when program is shutting down


        try{
            while(songList.isEmpty()){
                System.out.println("Input valid music directory");
                Path target = Paths.get(sc.nextLine());
                songList = Base.GetSongList(target);
            }

            IAudioPlayer audioPlayer= new WaveAudioPlayer(songList);
            audioPlayer.play();



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
                audioPlayer.goToChoice(input);
                if(input == 4) break;
            }
            sc.close();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
