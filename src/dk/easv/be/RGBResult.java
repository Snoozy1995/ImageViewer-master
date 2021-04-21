package dk.easv.be;

public class RGBResult {

    int red,green,blue,pixels;
    public RGBResult(int pixels, int red, int green, int blue){
        this.red=red;
        this.green=green;
        this.blue=blue;
        this.pixels=pixels;
    }

    public int getRed() {
        return red;
    }

    public double getRedPercentage(){
        return (double)Math.round(((double)red/pixels)*100);
    }

    public int getBlue() {
        return blue;
    }

    public double getBluePercentage(){
        return (double)Math.round(((double)blue/pixels)*100);
    }

    public int getGreen() {
        return green;
    }

    public double getGreenPercentage(){
        return (double)Math.round(((double)green/pixels)*100);
    }

    public int getMixed() {
        return (pixels-red-green-blue);
    }

    public double getMixedPercentage(){
        return (double)Math.round(((double)getMixed()/pixels)*100);
    }

    public int getPixels() {
        return pixels;
    }
}
