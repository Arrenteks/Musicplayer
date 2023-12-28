package de.medieninformatik.prog4;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Base {

    /**
     * Method for getting the Ending of a File like .wav, .mp3 and so on
     * @param path the path we want to extract from
     * @return the fileending
     */
    public static String GetFileEnding(String path){
        int lastIndex = path.lastIndexOf('.');
        if(lastIndex != -1){
            return path.substring(lastIndex);
        }

        return "unknown Ending";
    }

    /**
     * Converts files in a given Path to an Arraylist of Paths for the Player to play
     * @param target targetPath
     * @return ArrayList to play
     * @throws IOException is handeled via if, should not occur
     */
    public static ArrayList GetSongList(Path target) throws IOException {
        ArrayList<String> result = new ArrayList();
        if(Files.exists(target)){
            if(Files.isDirectory(target)){//convert music files from directory to a valid arraylist of files
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(target);
                for (Path path: directoryStream) {
                    if(Base.FileSupported(Base.GetFileEnding(path.toString()))){
                        result.add(path.toString());
                    }
                }
                directoryStream.close();
            }
            else if(Base.FileSupported(Base.GetFileEnding(target.toString()))){//check if single file is valid and add it
                result.add(target.toString());
            }
            else{
                System.err.println("No valid file input");
            }
        }
        else {
            System.err.println("Error 404: Path not found");
        }
        return result;
    }

    /**
     * checks if the Fileformat is supported by the music player
     * @param fileEnding Ending of the File
     * @return true if File supported, else false
     */
    public static boolean FileSupported(String fileEnding){
        switch(fileEnding){
            case ".wav", ".aiff", ".au": return true;
            default: return false;
        }
    }

    public static void writeObject(Object o){
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = new FileOutputStream("lastsong.txt");
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
