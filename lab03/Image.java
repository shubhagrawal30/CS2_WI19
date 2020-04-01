package edu.caltech.cs2.lab03;

import edu.caltech.cs2.libraries.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Image {
    private Pixel[][] pixels;

    public Image(File imageFile) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        this.pixels = new Pixel[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                this.pixels[i][j] = Pixel.fromInt(img.getRGB(i, j));
            }
        }
    }

    private Image(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    public Image transpose() {
        Pixel[][] temp = new Pixel[pixels[0].length][pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                temp[j][i] = pixels[i][j];
            }
        }
        return new Image(temp);
    }

    public String decodeText() {
        String bits = "";
        for (Pixel[] row: pixels)
            for(Pixel pix: row)
                bits += pix.getLowestBitOfR();


        String text = "";
        for (int i = 0; i <= bits.length() - 8; i+=8) {
            String bit = bits.substring(i, i+8);
            int value = Integer.parseInt(reverse(bit), 2);
            if (value == 0)
                continue;
            text += (char)value;
        }
        return text;
    }

    private String reverse(String word) {
        String rev = "";
        for (char c: word.toCharArray())
            rev = c + rev;
        return rev;
    }


    public Image hideText(String text) {

        List<Integer> bits = new ArrayList<>();

        for (char letter : text.toCharArray()){
            int dec = (int) letter;
            int i = 0;
            String binary = reverse(Integer.toBinaryString(dec));
            if (binary.length() > 8)
                binary = binary.substring(0,8);
            for(char bit : binary.toCharArray()) {
                bits.add(bit - '0');
                i++;
            }
            for(; i < 8; i++)
                bits.add(0);
        }



        Pixel[][] newPixels = this.pixels.clone();
        for (int i = 0, k = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                if (k < bits.size())
                    newPixels[i][j] = this.pixels[i][j].fixLowestBitOfR(bits.get(k++));
                else newPixels[i][j] = this.pixels[i][j].fixLowestBitOfR(0);
            }
        }

        return new Image(newPixels);
    }

    public BufferedImage toBufferedImage() {
        BufferedImage b = new BufferedImage(this.pixels.length, this.pixels[0].length, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                b.setRGB(i, j, this.pixels[i][j].toInt());
            }
        }
        return b;
    }

    public void save(String filename) {
        File out = new File(filename);
        try {
            ImageIO.write(this.toBufferedImage(), filename.substring(filename.lastIndexOf(".") + 1, filename.length()), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
