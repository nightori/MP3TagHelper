package mp3taghelper;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to MP3TagHelper!");
        System.out.print("Path to mp3 files: ");
        String path = scanner.nextLine();
        File[] files = Objects.requireNonNull(new File(path).listFiles(new Mp3Filter()));
        System.out.println("\n1 - rename files according to tags");
        System.out.println("2 - change tags according to filenames");
        System.out.print("Choose mode: ");
        switch(scanner.nextLine()){
            case "1":
                renameFiles(files);
                break;
            case "2":
                changeTags(files);
                break;
            default:
                System.out.println("Come on, are you a software tester or something?");
        }
        System.out.print("\nDone! Press enter to exit.");
        scanner.nextLine();
    }

    public static void renameFiles(File[] files){
        System.out.print("\nInclude artist name before track name? (y/n) ");
        String del = null;
        if (scanner.nextLine().equals("y")) {
            System.out.println("The pattern is \"ARTIST+DELIMITER+TITLE.mp3\"");
            System.out.print("Enter the delimiter (including spaces):");
            del = scanner.nextLine();
        }
        for (File f : files){
            System.out.print("\nProcessing \""+f.getName()+"\"");
            try {
                AudioFile audioFile = AudioFileIO.read(f);
                String filename = audioFile.getTag().getFirstTitle();
                if (del!=null) filename = audioFile.getTag().getFirstArtist() + del + filename;
                File newFile = new File(f.getParent()+"/"+filename+".mp3");
                boolean success = f.renameTo(newFile);
                if (!success) throw new IOException();
            } catch (Exception ex) {
                System.err.println("\nError: "+ex.toString());
            }
        }
    }

    public static void changeTags(File[] files){
        System.out.println("\n1 - just put filename in the title");
        System.out.println("2 - extract artist and track name, set both");
        System.out.print("Choose mode (again): ");
        String modeAgain = scanner.nextLine();
        String del = null;
        if (modeAgain.equals("2")) {
            System.out.println("\nThe pattern is \"ARTIST+DELIMITER+TITLE.mp3\"");
            System.out.println("All files must match this pattern!");
            System.out.print("Enter the delimiter (including spaces):");
            del = scanner.nextLine();
        }
        for (File f : files){
            System.out.print("\nProcessing \""+f.getName()+"\"");
            try {
                AudioFile audioFile = AudioFileIO.read(f);
                String fname = f.getName().replaceFirst("[.][^.]+$", "");
                String title = null;
                switch(modeAgain) {
                    case "1":
                        title = fname;
                        break;
                    case "2":
                        String[] split = fname.split(del);
                        if (split.length != 2) throw new IllegalArgumentException();
                        audioFile.getTag().setArtist(split[0]);
                        title = split[1];
                        break;
                    default:
                        System.out.println("Enjoy your nothing then!");
                        return;
                }
                audioFile.getTag().setTitle(title);
                audioFile.commit();
            } catch (Exception ex) {
                System.err.println("\nError while processing "+f.getName());
                System.err.println(ex.toString());
                return;
            }
        }
    }


}
