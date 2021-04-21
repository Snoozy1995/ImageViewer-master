package dk.easv.be;

import javafx.scene.image.Image;

import java.io.File;

public class SlideImage {
    String name;
    Image image;
    public SlideImage(File f){
        this.image=new Image(f.toURI().toString());
        this.name=f.getName();
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }
}
