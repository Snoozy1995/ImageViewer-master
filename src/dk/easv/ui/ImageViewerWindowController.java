package dk.easv.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import dk.easv.be.RGBResult;
import dk.easv.be.SlideImage;
import dk.easv.logic.ColorCounter;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController {
    private final List<SlideImage> images = new ArrayList<>();
    private int currentImageIndex = 0;

    @FXML private ImageView imageView;
    @FXML private Button btnToggleSlide;

    @FXML private Text currentFileText;
    @FXML private Text rgbStatsText;
    @FXML private Slider timeSlider;
    @FXML private Group btnGroup;

    boolean runSlide=false;
    ScheduledExecutorService slideExecutor = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> future;
    ExecutorService colorCountExecutor=Executors.newSingleThreadExecutor();

    @FXML
    void initialize(){
        timeSlider.setOnMouseReleased(event -> timerChange());
    }

    @FXML void handleBtnToggleSlide(){
        if(runSlide) {
            stopSlide();
            return;
        }
        if(images.isEmpty()) return;
        runSlide=true;
        btnToggleSlide.setText("Stop slideshow...");
        timerChange();
    }

    private void stopSlide(){
        runSlide=false;
        future.cancel(false);
        btnToggleSlide.setText("Start slideshow...");
    }

    private void timerChange(){
        if(!runSlide) return;
        if(future!=null){
            future.cancel(true);
        }
        future=slideExecutor.scheduleAtFixedRate(this::handleBtnNextAction,(int)timeSlider.getValue(),(int)timeSlider.getValue(), TimeUnit.SECONDS);
    }

    private void colorCount() {
        ColorCounter cc=new ColorCounter(images.get(currentImageIndex).getImage());
        RGBResult r=cc.getResult();
        rgbStatsText.setText("Red: "+r.getRed()+" - "+r.getRedPercentage()+"%\n"+
                             "Green: "+r.getGreen()+" - "+r.getGreenPercentage()+"%\n"+
                             "Blue: "+r.getBlue()+" - "+r.getBluePercentage()+"%\n"+
                             "Mixed: "+r.getMixed()+" - "+r.getMixedPercentage()+"%");
    }

    @FXML
    private void handleBtnLoadAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images","*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if(files==null||files.isEmpty()) return;
        files.forEach((File f)->images.add(new SlideImage(f)));
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