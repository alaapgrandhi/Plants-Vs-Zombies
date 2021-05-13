import java.awt.Image;
public class Projectile {
    int xlocation;
    int lastx;
    int ylocation;
    int damage;
    int speed;
    Image Pic;
    int width;
    int height;
    int duration = 10;
    boolean hasHit = false;
    boolean hasBurnt = false;
    Image hitPic;
    Projectile (int intX, int intY, int intDamage, Image imgPic, int intSpeed, int intheight, int intwidth, Image imgHit){
        xlocation = intX;
        lastx = xlocation;
        ylocation = intY;
        damage = intDamage;
        Pic = imgPic;
        speed = intSpeed;
        width = intwidth;
        height = intheight;
        hitPic = imgHit;
    }
    void burn(Image burntPic){
        damage = damage*2;
        Pic = burntPic;
        speed = (int)(speed*1.3);
        hasBurnt = true;
    }
    void hit(){
        Pic = hitPic;
        speed = 0;
        hasHit = true;
    }
}