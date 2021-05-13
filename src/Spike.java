import java.awt.Image;
public class Spike extends Plant implements Upgradeable{
    int state = 0;
    int refresh = 50;
    int damage = 5;
    Spike (int intHealth, int[] intLocation, Image imgPic)
    {
        super(intHealth, intLocation, imgPic);
    }
    public void upgrade() {
        state = 1;
        damage = 10;
        refresh = 20;
    }
}