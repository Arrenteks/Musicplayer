package de.medieninformatik.prog4;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WaveAudioPlayer implements IAudioPlayer {

    private long currentFrame;
    private Clip clip;
    private Status status;
    private Map<Integer, String> songMap;
    private Random rn;

    private AudioInputStream audioInputStream;
    private int currentFile;

    /**
     * Constructor for a Musicplayer Instance
     * @param filePaths List of Filepaths where music is stored
     * @throws UnsupportedAudioFileException Exception for when the Fileformat is not supported
     * @throws IOException misc Exception like FileNotFound
     * @throws LineUnavailableException Exceptionn for when the line is unavailable
     */
    public WaveAudioPlayer(List filePaths) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        currentFile = 0;
        songMap = fillSongList(filePaths);

        audioInputStream = AudioSystem.getAudioInputStream(new File(songMap.get(currentFile)).getAbsoluteFile());

        clip = AudioSystem.getClip();

        clip.addLineListener(evt -> {
            currentFrame = clip.getMicrosecondPosition();
            if (evt.getType() == LineEvent.Type.STOP && currentFrame == clip.getMicrosecondLength()) {
                currentFile++;
                restart();
            }
        });
        clip.open(audioInputStream);
    }


    /**
     * filling an ArrayList into a HashMap for better access within the player
     * @param songList list of Songs to convert into Map
     * @return Map of Songs
     */
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
     */
    public void goToChoice(int choice){
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

    /**
     * skips the current song by increasing the currentfile variable and reseting the AudioStream
     */
    public void skip(){
        if(currentFile < songMap.size()-1){
            clip.close();
            currentFile++;
            try {
                resetAudioStream(0);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * play the previous song again by decreasing currentfile by 1 and reseting the AudioStream
     */
    public void previous(){
        if(currentFile > 0){
            clip.stop();
            clip.close();
            currentFile--;
            try {
                resetAudioStream(0);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * play the song at the current value of currentfile and parse the status PLAY
     */
    public void play(){
        clip.start();
        status = Status.PLAY;
    }

    /**
     * pause the song at the current value of currentfile and parse the status STOP
     */
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

    /**
     * resume the song at the current value of currentfile and parse the status PLAY
     */
    public void resumeAudio(){
        if(status.equals(Status.PLAY)){
            System.out.println("Audio is already playing");
            return;
        }

        status = Status.PLAY;
        clip.close();
        try {
            resetAudioStream(currentFrame);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        this.play();
    }

    /**
     * restart the current played song by reseting the AudioStream
     * @throws UnsupportedAudioFileException Exception for when the Fileformat is not supported
     * @throws IOException misc Exception like FileNotFound
     * @throws LineUnavailableException Exceptionn for when the line is unavailable
     */
    public void restart(){
        clip.stop();
        clip.close();
        try {
            resetAudioStream(0);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        this.play();
    }

    /**
     * stop the playing of the song - Ends the execution of the program
     */
    public void stop(){
        clip.stop();
        clip.close();
        currentFrame = 0L;
    }

    /**
     * jump to the parsed Milisecond value of the current song
     * @param milisec value to jump to
     */
    public void jump(long milisec){
        if( milisec > 0 && milisec < clip.getMicrosecondLength()){
            clip.stop();
            clip.close();
            try {
                resetAudioStream(milisec);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            this.play();
        }
    }

    /**
     * resets the current Audiostream at a defined point in the stream
     * @throws UnsupportedAudioFileException Exception for when the Fileformat is not supported
     * @throws IOException misc Exception like FileNotFound
     * @throws LineUnavailableException Exceptionn for when the line is unavailable
     */
    private void resetAudioStream(long frame) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        audioInputStream = AudioSystem.getAudioInputStream(new File(songMap.get(currentFile)).getAbsoluteFile());
        clip.open(audioInputStream);
        clip.setMicrosecondPosition(frame);
        clip.start();
    }

    /**
     * pick a random number within the Bounds of the songmapsize and play the song
     * @throws UnsupportedAudioFileException Exception for when the Fileformat is not supported
     * @throws IOException misc Exception like FileNotFound
     * @throws LineUnavailableException Exceptionn for when the line is unavailable
     */
    @Override
    public void random(){
        if(rn == null) rn = new Random();//check if randomizer already exists

        clip.stop();
        clip.close();
        currentFile = rn.nextInt(songMap.size());
        try {
            resetAudioStream(0);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

}
