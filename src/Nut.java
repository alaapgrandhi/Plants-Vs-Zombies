import java.awt.Image;
public class Nut extends Plant {
    Image SlightlyDamagedPic;
    Image HeavilyDamagedPic;
    Nut (int intHealth, int[] intLocation, Image imgPic, Image imgDamaged1, Image imgDamaged2)
    {
        super(intHealth, intLocation, imgPic);
        SlightlyDamagedPic = imgDamaged1;
        HeavilyDamagedPic = imgDamaged2;
    }
    @Override
    boolean Eaten(){
        health--;
        if (health == 480){
            Pic = SlightlyDamagedPic;
        }
        else if (health == 240){
            Pic = HeavilyDamagedPic;
        }
        return (health == 0);
    } 
}