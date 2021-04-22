package dk.easv.logic;

import dk.easv.be.RGBResult;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ColorCounter extends Task<Void> {

    Image image;

    public ColorCounter(Image image){
        this.image=image;
    }

    @Override
    protected Void call() {
        PixelReader p=image.getPixelReader();
        int w = (int) image.getWidth(), h = (int) image.getHeight(), pixelCount=w*h,
                blueCount=0,greenCount=0,redCount=0;
        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if(this.isCancelled()){
                    return null;
                }
                Color myColor = p.getColor(x, y);
                if (myColor.getBlue() > myColor.getRed() && myColor.getBlue() > myColor.getGreen()) {
                    blueCount++;
                } else if (myColor.getRed() > myColor.getBlue() && myColor.getRed() > myColor.getGreen()) {
                    redCount++;
                } else if (myColor.getGreen() > myColor.getRed() && myColor.getGreen() > myColor.getBlue()) {
                    greenCount++;
                }
                RGBResult r=new RGBResult(pixelCount,redCount,greenCount,blueCount);
                this.updateMessage("Red: " + r.getRed() + " - " + r.getRedPercentage() + "%\n" +
                        "Green: " + r.getGreen() + " - " + r.getGreenPercentage() + "%\n" +
                        "Blue: " + r.getBlue() + " - " + r.getBluePercentage() + "%\n" +
                        "Mixed: " + r.getMixed() + " - " + r.getMixedPercentage() + "%");
            }
        }
        return null;
    }
}
