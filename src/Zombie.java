public class Zombie {
    int xlocation;
    int ylocation;
    int health = 100;
    int speed;
    int movement_modifier = 1;
    Zombie (int intxLoc, int intyLoc, int intSpeed, int healthModi){
        xlocation = intxLoc;
        ylocation = intyLoc;
        speed = intSpeed;
        health *= healthModi;
    }
    void eat(){
        movement_modifier = 0;
    }
    void move(){
        movement_modifier = 1;
    }
}