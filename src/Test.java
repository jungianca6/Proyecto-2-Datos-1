

import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


public class Test {
    public static void main(String[] args) {
        Tesseract tesseract = new Tesseract();
        try {

            tesseract.setDatapath("C:\\Users\\jungi\\Desktop\\Tesseract\\Tess4J-3.4.8-src\\Tess4J\\tessdata");

            //tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            // the path of your tess data folder
            // inside the extracted file

            String text
                    = tesseract.doOCR(new File("test.png"));

            // path of your image file
            System.out.print(text);
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}