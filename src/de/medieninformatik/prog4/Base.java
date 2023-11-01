package de.medieninformatik.prog4;

public class Base {

    /**
     * Method for getting the Ending of a File like .wav, .mp3 and so on
     * @param path the path we want to extract from
     * @return the fileending
     */
    public static String GetFileEnding(String path){
        int lastIndex = path.lastIndexOf('.');
        String ending = path.substring(lastIndex);
        return ending;
    }
}
