import java.awt.Image;
public class Sunflower extends Plant{
    int refresh = 500;
    Sunflower (int intHealth, int[] intLocation, Image imgPic)
    {
        super(intHealth, intLocation, imgPic);
    }
    Sun Produce(Image imgPic, int intSpeed, int intHeight, int intWidth)
    {
        Sun light = new Sun(location[1], location[0], imgPic, intSpeed, intHeight, intWidth);
        return light;
    }
}