import java.awt.Image;
public class Sun {
    int xlocation;
    int ylocation;
    int duration = 100;
    int width;
    int height;
    int speed;
    Image Pic; 
    Sun (int intX, int intY, Image imgPic, int intSpeed, int intHeight, int intWidth){
        xlocation = intX;
        ylocation = intY;
        width = intWidth;
        height = intHeight;
        speed = intSpeed;
        Pic = imgPic;
    }
}