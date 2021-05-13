import java.awt.Image;
public class Plant {
    int health;
    int[] location;
    Image Pic;
    Plant (int intHealth, int[] intLocation, Image imgPic)
    {
        health = intHealth;
        location = intLocation;
        Pic = imgPic;
    }
    boolean Eaten()
    {
        health--;
        return (health<=0);
    }
}