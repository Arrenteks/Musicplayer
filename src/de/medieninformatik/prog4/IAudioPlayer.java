package de.medieninformatik.prog4;

public interface IAudioPlayer {

    void skip();
    void previous();
    void play();
    void pause();
    void resumeAudio();
    void restart();
    void stop();
    void jump(long milisecs);
    void random();

    void goToChoice(int input);
}
