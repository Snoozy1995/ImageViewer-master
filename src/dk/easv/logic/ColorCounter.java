package dk.easv.logic;

import dk.easv.be.RGBResult;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ColorCounter{

    RGBResult result;

    public ColorCounter(Image image){
        PixelReader p=image.getPixelReader();
        int w = (int) image.getWidth(), h = (int) image.getHeight(), pixelCount=w*h,
                blueCount=0,greenCount=0,redCount=0;
        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color myColor = p.getColor(x, y);
                if (myColor.getBlue() > myColor.getRed() && myColor.getBlue() > myColor.getGreen()) {
                    blueCount++;
                } else if (myColor.getRed() > myColor.getBlue() && myColor.getRed() > myColor.getGreen()) {
                    redCount++;
                } else if (myColor.getGreen() > myColor.getRed() && myColor.getGreen() > myColor.getBlue()) {
                    blueCount++;
                }
            }
        }
        result=new RGBResult(pixelCount,redCount,greenCount,blueCount);
    }

    public RGBResult getResult() {
        return result;
    }
}
