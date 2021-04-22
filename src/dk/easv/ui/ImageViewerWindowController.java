package dk.easv.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
    boolean runSlide=false;

    @FXML private ImageView imageView;
    @FXML private Button btnToggleSlide;

    @FXML private Text currentFileText;
    @FXML private Text rgbStatsText;
    @FXML private Slider timeSlider;
    @FXML private Group btnGroup;

    ScheduledExecutorService slideExecutor = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> future;

    @FXML
    void initialize(){
        timeSlider.setOnMouseReleased(event -> resetTimer());
    }

    @FXML
    void handleBtnToggleSlide(){
        if(runSlide) { //If slide is running already, stop it and return.
            stopSlide();
            return;
        }
        if(images.isEmpty()) return; //Check that images isnt empty.
        runSlide=true;
        btnToggleSlide.setText("Stop slideshow...");
        resetTimer();
    }

    @FXML
    private void handleBtnLoadAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images","*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        images.clear();
        files.forEach((File f)->images.add(new SlideImage(f)));
        btnGroup.setDisable(false);
        displayImage();
    }

    @FXML
    private void handleBtnPreviousAction(){
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
        displayImage();
        resetTimer();
    }

    @FXML
    private void handleBtnNextAction(){
        nextSlide();
        resetTimer();
    }

    private void stopSlide(){
        runSlide=false;
        future.cancel(false); //Cancel any scheduled
        btnToggleSlide.setText("Start slideshow...");
    }

    private void resetTimer(){
        if(!runSlide) return;
        if(future!=null){
            //Cancel any prior scheduled.
            future.cancel(true);
        }
        //Schedule thread for slideshow
        future=slideExecutor.scheduleAtFixedRate(this::nextSlide,(int)timeSlider.getValue(),(int)timeSlider.getValue(), TimeUnit.SECONDS);
    }

    private void nextSlide(){
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex + 1) % images.size();
        displayImage();
    }

    private void displayImage(){
        if (images.isEmpty()) return;
        SlideImage image =images.get(currentImageIndex); //Get current SlideImage object
        currentFileText.setText("Current file: "+image.getName()); //Get the filename to set text
        imageView.setImage(image.getImage()); //Set image in view

        ColorCounter cc=new ColorCounter(image.getImage());
        cc.messageProperty().addListener((o,old,n)->rgbStatsText.setText(n));
        ExecutorService exe = Executors.newSingleThreadExecutor();
        exe.submit(cc);
    }
}