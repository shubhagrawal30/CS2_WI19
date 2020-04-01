package edu.caltech.cs2.lab03;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageManipulator {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename you'd like to work with: ");
        String inFilename = scanner.nextLine();
        // get the filename without the .png
        String[] splitfile = inFilename.split("\\.");
        String name = splitfile[0];

        // turn the file into an image
        File in = new File("images/" + inFilename);
        Image img = new Image(in);

        boolean keepAsking = true;
        while(keepAsking) {
            System.out.print("What would you like to do wih the image (transpose, hide, decode or quit)? ");
            String action = scanner.nextLine();

            if (action.equals("transpose")) {
                String outFilename = name + ".transpose.png";
                //transpose image and save it
                Image transposedImage = img.transpose();
                transposedImage.save("images/" + outFilename);
                System.out.println("Image was transposed!");
            }
            else if (action.equals("hide")) {
                System.out.print("What message would you like to hide? ");
                String textToHide =  scanner.nextLine();
                // setup the output file
                String outFilename = name + ".hidden.png";

                // output file with hidden text
                Image imgWithText = img.hideText(textToHide);
                imgWithText.save("images/" + outFilename);
                System.out.println("Message was hidden!");
            }
            else if (action.equals("decode")) {
                System.out.println("The hidden message is: " + img.decodeText());
            }
            else if (action.equals("quit")) {
                keepAsking = false;
            }
            else {
                System.out.println("Invalid input.");
            }
        }
    }
}
