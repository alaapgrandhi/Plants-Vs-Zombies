import java.awt.Image;
public class Peashooter extends Plant{
    int refresh = 150;
    Peashooter (int intHealth, int[] intLocation, Image imgPic)
    {
        super(intHealth, intLocation, imgPic);
    }
    Projectile Shoot(Image peaPic, int height, int width, int plantwidth, int speed, Image hitPic)
    {
        Projectile pea = new Projectile(location[1]+plantwidth-10, location[0]+10, 15, peaPic, speed, width, height, hitPic);
        return pea;
    }
}