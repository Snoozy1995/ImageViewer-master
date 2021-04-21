package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController {
    private final List<SlideImage> images = new ArrayList<>();
    private int currentImageIndex = 0;

    @FXML private ImageView imageView;
    @FXML private Button btnStartSlide;

    @FXML private Text currentFileText;
    @FXML private Text rgbStatsText;
    @FXML private Slider timeSlider;
    @FXML private Group btnGroup;

    boolean runSlide=false;
    ExecutorService slideExecutor = Executors.newSingleThreadExecutor(); //@todo rewrite to use task approach.
    ExecutorService colorCountExecutor=Executors.newSingleThreadExecutor(); //@todo rewrite to use task approach.

    @FXML void handleBtnStartSlide(){
        if(runSlide){
            stopSlide();
            return;
        }
        if(images.isEmpty()) return;
        runSlide=true;
        btnStartSlide.setText("Stop slideshow...");
        slideExecutor.submit(()->{
            while(runSlide){
                handleBtnNextAction();
                try {
                    Thread.sleep(((int)timeSlider.getValue())*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopSlide(){
        runSlide=false;
        btnStartSlide.setText("Start slideshow...");
    }

    private void colorCount() {
        Image image=images.get(currentImageIndex).getImage();
        PixelReader p=image.getPixelReader();
        int w = (int) image.getWidth(), h = (int) image.getHeight(), pixelCount=w*h,
                blueCount=0,greenCount=0,redCount=0;
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                Color mycolor = p.getColor(x, y);
                if(mycolor.getBlue()> mycolor.getRed()&&mycolor.getBlue()> mycolor.getGreen()){
                    blueCount++;
                }else if(mycolor.getRed()> mycolor.getBlue()&&mycolor.getRed()> mycolor.getGreen()){
                    redCount++;
                }else if(mycolor.getGreen()> mycolor.getRed()&&mycolor.getGreen()>mycolor.getBlue()){
                    blueCount++;
                }
            }
        }
        rgbStatsText.setText("Red: "+redCount+" - "+(double)Math.round(((double)redCount/pixelCount)*100)+"%\n"+
                "Green: "+greenCount+" - "+(double)Math.round(((double)greenCount/pixelCount)*100)+"%\n"+
                "Blue: "+blueCount+" - "+(double)Math.round(((double)blueCount/pixelCount)*100)+"%\n"+
                "Mixed: "+(pixelCount-redCount-greenCount-blueCount)+" - "+(double)Math.round(((double)(pixelCount-redCount-greenCount-blueCount)/pixelCount)*100)+"%");
    }

    @FXML
    private void handleBtnLoadAction(){
        stopSlide();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if (files.isEmpty()) return;
        files.forEach((File f)->images.add(new SlideImage(f)));
        if(images.isEmpty()){
            btnGroup.setDisable(true);
            return;
        }
        btnGroup.setDisable(false);
        displayImage();
    }

    @FXML
    private void handleBtnPreviousAction(){
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
        displayImage();
    }

    @FXML
    private void handleBtnNextAction(){
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex + 1) % images.size();
        displayImage();
    }

    private void displayImage(){
        if (images.isEmpty()) return;
        SlideImage image =images.get(currentImageIndex);
        currentFileText.setText("Current file: "+image.getName());
        imageView.setImage(image.getImage());
        colorCountExecutor.submit(this::colorCount);
    }
}