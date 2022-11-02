package de.medieninformatik.prog4;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SimpleAudioPlayer {

    private long currentFrame;
    private Clip clip;
    private Status status;
    private Map<Integer, String> songMap;
    private Random rn;

    private AudioInputStream audioInputStream;
    private int currentFile;

    public SimpleAudioPlayer(List filePaths) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        currentFile = 0;
        songMap = fillSongList(filePaths);

        audioInputStream = AudioSystem.getAudioInputStream(new File(songMap.get(currentFile)).getAbsoluteFile());

        clip = AudioSystem.getClip();

        addLineListener();
        clip.open(audioInputStream);


    }

    /**
     * Adds Line Listener to the File. Checks, when the clip is paused if the end of file was reached. If that is the case
     * the player skips to the next File and restarts the clip.
     */
    private void addLineListener(){
        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                currentFrame = clip.getMicrosecondPosition();
                if (event.getType() == LineEvent.Type.STOP && currentFrame == clip.getMicrosecondLength()) {
                    try {
                        currentFile++;
                        restart();
                    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    private Map<Integer, String> fillSongList(List songList){
        Map<Integer, String> songMap = new HashMap<>();
        for(int i = 0; i < songList.size(); i++){
            songMap.put(i, songList.get(i).toString());
        }

        return songMap;
    }

    /**
     * simple interface for interaction with the song.
     * TODO : Replace by a GUI
     * @param choice chosen option
     * @throws UnsupportedAudioFileException Filetype was not supported by the player
     * @throws LineUnavailableException Line was unavailable when the choice was made
     * @throws IOException misc. Exceptions like FileNotFound
     */
    public void goToChoice(int choice) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        switch (choice){
            case 1: pause();
            break;
            case 2: resumeAudio();
            break;
            case 3: restart();
            break;
            case 4: stop();
            break;
            case 5:
                System.out.println("Enter time (" + 0 + ", " + clip.getMicrosecondLength() + ")");
                Scanner sc = new Scanner(System.in);
                long c1 = sc.nextLong();
                jump(c1);
                break;
            case 6: skip();
            break;
            case 7: previous();
            break;
            case 8: random();
            break;
        }
    }

    public void skip() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if(currentFile < songMap.size()-1){
            clip.close();
            currentFile++;
            resetAudioStream();
        }

    }

    public void previous() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if(currentFile > 0){
            clip.stop();
            clip.close();
            currentFile--;
            resetAudioStream();
        }
    }

    public void play(){
        clip.start();
        status = Status.PLAY;
    }

    public void pause(){
        if(status.equals(Status.STOP)){
            System.out.println("Audio is already paused.");
            return;
        }
        status = Status.STOP;
        this.currentFrame = this.clip.getMicrosecondPosition();
        clip.stop();
        clip.close();

    }

    public void resumeAudio() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if(status.equals(Status.PLAY)){
            System.out.println("Audio is already playing");
            return;
        }

        status = Status.PLAY;
        clip.close();
        resetAudioStream();
        clip.setMicrosecondPosition(currentFrame);
        this.play();
    }

    public void restart() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        clip.stop();
        clip.close();
        resetAudioStream();
        clip.setMicrosecondPosition(0);
        this.play();
    }

    public void stop(){
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    public void jump(long milisec) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if( milisec > 0 && milisec < clip.getMicrosecondLength()){
            clip.stop();
            clip.close();
            resetAudioStream();
            currentFrame = milisec;
            clip.setMicrosecondPosition(milisec);
            this.play();
        }
    }

    /**
     * resets the current Audiostream, by opening the currentfile and starting the clip
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    private void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        audioInputStream = AudioSystem.getAudioInputStream(new File(songMap.get(currentFile)).getAbsoluteFile());
        clip.open(audioInputStream);
        clip.start();
    }

    private void random() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if(rn == null) rn = new Random();//check if randomizer already exists

        clip.stop();
        clip.close();
        currentFile = rn.nextInt(songMap.size());
        resetAudioStream();
    }

}
