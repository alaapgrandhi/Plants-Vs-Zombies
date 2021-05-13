import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.HashMap;
import javax.swing.JButton;
public class PVZ {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Plants Vs Zombies");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.add(new gameloop());
        frame.setVisible(true);
    }
}
class gameloop extends JComponent implements ActionListener{
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    HashMap<String,Image> imageHashMap = new HashMap<String,Image>();
    String[] imageStrings = {"Explode", "Fire_Pea", "Pea", "Lawn", "Peashooter_Packet", "Peashooter", "Packet_Bar", "Shovel", "Spikerock_Packet", "Spikerock", "Spikeweed_Packet", "Spikeweed", "Sun", "Sunflower", "Sunflower_Packet", "Torchwood", "Torchwood_Packet", "Wallnut_Full", "Wallnut_Dented", "Wallnut_Cracked", "Wallnut_Packet", "Zombie_1", "Zombie_2"};
    String[] packetStrings = {"Peashooter_Packet", "Spikerock_Packet", "Spikeweed_Packet", "Sunflower_Packet", "Torchwood_Packet", "Wallnut_Packet"};
    int[] packetCosts = {100, 125, 100, 50, 175, 50};
    String chosenPlant = "";
    int chosenCost = 0;
    int money = 250;
    public ArrayList<Nut> wallnuts = new ArrayList<Nut>();
    public ArrayList<Peashooter> peashooters = new ArrayList<Peashooter>();
    public ArrayList<Stump> stumps = new ArrayList<Stump>();
    public ArrayList<Sunflower> sunflowers = new ArrayList<Sunflower>();
    public ArrayList<Spike> spikes = new ArrayList<Spike>();
    public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    public ArrayList<Sun> sunlight = new ArrayList<Sun>();
    public ArrayList<Zombie> zombies = new ArrayList<Zombie>();
    public ArrayList<int[]> occupiedLocations = new ArrayList<int[]>();
    Graphics2D graphicsObject;
    int barWidth = (int)(0.49371*screenSize.getWidth());
    int barHeight = (int)(0.14202*screenSize.getHeight());
    int barSpace = Math.max((int)(0.0066824*screenSize.getWidth()), 1);
    int packetSpace = 3;
    int packetWidth = (int)(0.062745*screenSize.getWidth());
    int packetHeight = (int)(0.11640*screenSize.getHeight());
    int packetLeft = (int)(0.10118*screenSize.getWidth());
    int packetTop = Math.max((int)(0.013228*screenSize.getHeight()), 1);
    int leftLawn = (int)(screenSize.width*0.24175);
    int topLawn = (int)(screenSize.height*0.26161);
    int spotHeight = (int)(screenSize.height*0.13412);
    int spotWidth = (int)(screenSize.width*0.080093);
    int peaHeight = (int)(spotHeight/3);
    int peaWidth = (int)(spotWidth/3);
    int plantbuffer = Math.max((int)(screenSize.height*0.0080085), 1);
    int sunStringWidth = (int)(barWidth*0.076465);
    int sunStringHeight = (int)(barHeight*0.86365);
    int buffer = Math.max((int)(screenSize.width*0.0013889), 1);
    int horiSpeed = (int)(screenSize.width*0.01);
    int vertSpeed = Math.max((int)(screenSize.height*0.0025), 1);
    int numWaves = 3;
    int currWave = 1;
    int Zombrefresh = 600;
    int Zombdelay = 800;
    int numZombs = 5;
    int gameLost = 0;
    int gameWon = 0;
    int zombSpeedModifier = 1;
    int zombHealthModifier = 1;
    boolean suncollectindicator = true;
    int sunRefresh = 1000;
    int zomRefresh = 0;
    public gameloop() {
        setup();
        Thread animationThread = new Thread(new Runnable(){
            public void run(){
                while (true){
                    repaint();
                    try{
                        Thread.sleep(30);
                    }
                    catch (Exception e) {}
                }
            }
        });
        animationThread.start();
    }
    public void paintComponent(Graphics g){
        graphicsObject = (Graphics2D) g;
        background();
        plantMove();
        projectileMove();
        zombieMove();
        sunRefresh -= 1;
        if(sunRefresh == 0){
            naturalSun();
        }
        Zombrefresh -= 1;
        if(Zombrefresh == 0){
            zombSpawn();
        }
    }
    public void zombieMove(){
        ArrayList<Zombie> removals = new ArrayList<Zombie>();
        zomRefresh++;
        for (Zombie zom : zombies){
            zom.xlocation = zom.xlocation - (zom.speed*zom.movement_modifier);
            if (zom.health <= 0){
                removals.add(zom);
            }
            else{
                if (Math.floor(zomRefresh/15)%2 == 0)
                {
                    graphicsObject.drawImage(imageHashMap.get("Zombie_1"), zom.xlocation, zom.ylocation, spotWidth, (int)(spotHeight*1.1), this);
                }
                else{
                    graphicsObject.drawImage(imageHashMap.get("Zombie_2"), zom.xlocation, zom.ylocation, spotWidth, (int)(spotHeight*1.1), this);
                }
                if (zomRefresh >= 10000){
                    zomRefresh = 0;
                }
            }
            if (zom.xlocation <= (int)(leftLawn-(0.5*spotWidth)) && gameLost == 0 && gameWon == 0){
                gameLost = 1;
                gameEndLose();
            }
            if (zom.xlocation <= 0){
                removals.add(zom);
            }
        }
        for (Zombie zom : removals){
            zombies.remove(zom);
        }
    }
    public void projectileMove(){
        ArrayList<Projectile> removals = new ArrayList<Projectile>();
        for (Projectile proj: projectiles){
            if (proj.hasHit){
                proj.duration -= 1;
                if (proj.duration == 0){
                    removals.add(proj);
                }
                else{
                    graphicsObject.drawImage(proj.Pic, proj.xlocation, proj.ylocation, proj.width, proj.height, this);
                }
            }
            else{
            proj.lastx = proj.xlocation;
            proj.xlocation = proj.lastx + proj.speed;
            if (!proj.hasBurnt){
            for (Stump stum: stumps){
                if (stum.location[0] == (proj.ylocation-10) && proj.lastx <= stum.location[1] && proj.xlocation >= stum.location[1]){
                    proj.burn(imageHashMap.get("Fire_Pea"));
                }
            }
            }
            for (Zombie zom: zombies){
                if ((zom.ylocation+(int)(0.1*spotHeight)) == (proj.ylocation-10) && zom.xlocation>= proj.lastx && zom.xlocation <= proj.xlocation && (!proj.hasHit)){
                    proj.hit();
                    zom.health -= proj.damage;
                }
            }
            if (proj.xlocation > screenSize.width){
                removals.add(proj);
            }
            else
            {
                graphicsObject.drawImage(proj.Pic, proj.xlocation, proj.ylocation, proj.width, proj.height, this);
            }
        }
        }
        for (Projectile proj: removals){
            projectiles.remove(proj);
        }
        ArrayList<Sun> removals2 = new ArrayList<Sun>();
        for (Sun sn : sunlight){
            sn.ylocation = sn.ylocation - sn.speed;
            sn.duration -= 1;
            if (sn.duration == 0){
                removals2.add(sn);
            }
            else{
                graphicsObject.drawImage(sn.Pic, sn.xlocation, sn.ylocation, sn.width, sn.height, this);
            }
        }
        for (Sun sn : removals2){
            sunlight.remove(sn);
        }
    }
    public boolean Eaten(Plant plants){
        boolean answer = false;
        for (Zombie zom : zombies){
            if ((zom.xlocation - zom.speed) < plants.location[1] && (zom.xlocation + zom.speed) > plants.location[1] && (zom.ylocation+(int)(0.1*spotHeight)) == plants.location[0]){
                if (plants.Eaten()){
                    zom.move();
                    answer = true;
                }
                else{
                    zom.eat();
                }
            }
        }
        if (answer){
            int i = 0;
            for (Zombie zom : zombies){
                if ((zom.xlocation - zom.speed) < plants.location[1] && (zom.xlocation + zom.speed) > plants.location[1] && (zom.ylocation+(int)(0.1*spotHeight)) == plants.location[0]){
                    zom.move();
                    zom.xlocation += (i*5);
                    i++;
                }
            }
            for (int[] loc: occupiedLocations){
                if (Arrays.equals(loc, plants.location)){
                    occupiedLocations.remove(loc);
                    break;
                }
            }
        }
        return answer;
    }
    public void plantMove(){
        ArrayList<Peashooter> removalsPea = new ArrayList<Peashooter>();
        for (Peashooter pea: peashooters){
            graphicsObject.drawImage(pea.Pic, pea.location[1], pea.location[0], spotWidth - (2*plantbuffer), spotHeight - plantbuffer, this);
            pea.refresh = pea.refresh-1;
            if (pea.refresh == 0){
                projectiles.add(pea.Shoot(imageHashMap.get("Pea"), peaHeight, peaWidth, spotWidth, horiSpeed, imageHashMap.get("Explode")));
                pea.refresh = 150;
            }
            if (Eaten(pea)){
                removalsPea.add(pea);
            }
        }
        for (Peashooter pea : removalsPea){
            peashooters.remove(pea);
        }
        ArrayList<Sunflower> removalsSun = new ArrayList<Sunflower>();
        for (Sunflower sun: sunflowers){
            graphicsObject.drawImage(sun.Pic, sun.location[1], sun.location[0], spotWidth - (2*plantbuffer), spotHeight - plantbuffer, this);
            sun.refresh = sun.refresh-1;
            if (sun.refresh == 0){
                sunlight.add(sun.Produce(imageHashMap.get("Sun"), vertSpeed, spotWidth, spotWidth));
                sun.refresh = (int)((Math.random()*200))+400;
            }
            if (Eaten(sun)){
                removalsSun.add(sun);
            }
        }
        for (Sunflower suns : removalsSun){
            sunflowers.remove(suns);
        }
        ArrayList<Nut> removalsNut = new ArrayList<Nut>();
        for (Nut wall: wallnuts){
            graphicsObject.drawImage(wall.Pic, wall.location[1]+(int)(spotWidth*0.15), wall.location[0], spotWidth - (2*plantbuffer), spotHeight - plantbuffer, this);
            if (Eaten(wall)){
                removalsNut.add(wall);
            }
        }
        for (Nut wall : removalsNut){
            wallnuts.remove(wall);
        }
        ArrayList<Stump> removalsStump = new ArrayList<Stump>();
        for (Stump stum: stumps){
            graphicsObject.drawImage(stum.Pic, stum.location[1], stum.location[0], spotWidth - (2*plantbuffer), spotHeight - plantbuffer, this);
            if (Eaten(stum)){
                removalsStump.add(stum);
            }
        }
        for (Stump stum : removalsStump){
            removalsStump.remove(stum);
        }
        for (Spike spi: spikes){
            graphicsObject.drawImage(spi.Pic, spi.location[1]+(int)(spotWidth*0.1), spi.location[0]+(int)(spotHeight*0.1), spotWidth - (2*plantbuffer), spotHeight - plantbuffer, this);
            spi.refresh -= 1;
            if (spi.refresh == 0){
                spi.refresh = 50;
                for (Zombie zom : zombies)
                {
                    if (zom.xlocation >= (spi.location[1]-spotWidth) && zom.xlocation <= (spi.location[1] + spotWidth - (2*plantbuffer)) && (zom.ylocation+(int)(0.1*spotHeight)) == spi.location[0]){
                        zom.health -= spi.damage;
                    }
                }
            }
        }
    }
    public void background(){
        graphicsObject.drawImage(imageHashMap.get("Lawn"), 0, 0, screenSize.width, screenSize.height, this);
        graphicsObject.drawImage(imageHashMap.get("Packet_Bar"), barSpace, 0, barWidth, barHeight, this);
        graphicsObject.drawImage(imageHashMap.get("Shovel"), barSpace+barWidth, 0, barHeight, barHeight, this);
        graphicsObject.drawString("" + money + "", sunStringWidth, sunStringHeight);
        for (int i = 0; i < packetStrings.length; i++){
            graphicsObject.drawImage(imageHashMap.get(packetStrings[i]), packetLeft + (i*(packetWidth+packetSpace)), packetTop, packetWidth, packetHeight, this);
        }
    }
    public void setup(){
        super.addMouseListener(mouseLtnr);
        try{
            for (String pic: imageStrings){
                /*File img = new File(("resources/" + pic + ".jpg"));
                Image picturImage = ImageIO.read(img);*/
                Image picturImage = ImageIO.read(gameloop.class.getResourceAsStream(("resources/" + pic + ".jpg")));
                imageHashMap.put(pic, picturImage);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        buttonsetup();
    }
    public void buttonsetup(){
        for (int i = 0; i<5; i++){
            for (int j = 0; j<9; j++){
                JButton button1 = new JButton("");
                button1.setBounds(leftLawn + (spotWidth*j), topLawn + (spotHeight*i), spotWidth, spotHeight);
                button1.setEnabled(true);
    		    button1.setOpaque(false);
    		    button1.setContentAreaFilled(false);
    		    button1.setBorderPainted(false);
    		    button1.setActionCommand("Row: " + (i+1) + ",Column: " + (j+1));
    		    button1.addActionListener(this);
                add(button1);
            }
        }
        for (int i = 0; i<packetStrings.length; i++){
            JButton button1 = new JButton("");
            button1.setBounds(packetLeft + (i*(packetWidth+packetSpace)), packetTop, packetWidth, packetHeight);
            button1.setEnabled(true);
    		button1.setOpaque(false);
    		button1.setContentAreaFilled(false);
    		button1.setBorderPainted(false);
    		button1.setActionCommand("Type: " + packetStrings[i] + ",Cost: " + packetCosts[i]);
    		button1.addActionListener(this);
            add(button1);
        }
        JButton button1 = new JButton("");
        button1.setBounds(barSpace+barWidth, 0, barHeight, barHeight);
        button1.setEnabled(true);
    	button1.setOpaque(false);
		button1.setContentAreaFilled(false);
        button1.setBorderPainted(false);
    	button1.setActionCommand("Shovel");
    	button1.addActionListener(this);
        add(button1);
    }
    public void actionPerformed (ActionEvent e)
    {
        processMouse(super.getMousePosition().x, super.getMousePosition().y);
        if (suncollectindicator){
        if ((e.getActionCommand()).startsWith("Type")){
            int endindex1 = 0;
            int startindex2 = 0;
            for (int i = 6; i<e.getActionCommand().length(); i++){
                if (e.getActionCommand().charAt(i) == ' '){
                    startindex2 = i+1;
                    break; 
                }
                else if (e.getActionCommand().charAt(i) == '_'){
                    endindex1 = i;
                }
            }
            chosenPlant = e.getActionCommand().substring(6, endindex1);
            chosenCost = Integer.parseInt(e.getActionCommand().substring(startindex2));
        }
        else if ((e.getActionCommand()).startsWith("Row")){
            if (!chosenPlant.equals("")){
                if (money>=chosenCost){
                    if (chosenPlant.equals("Shovel")){
                        Remove(e.getActionCommand());
                    }
                    else{
                        Planting(e.getActionCommand());
                    }
                }}
        }
        else if ((e.getActionCommand()).equals("Shovel")){
            chosenPlant = "Shovel";
            chosenCost = 0;
        }
        }
        else{
            suncollectindicator = true;
        }
    }
    public void Remove(String plant){
        int xloc = 0;
        int yloc = 0;
        int indy = 0;
        for (int i = 5; i<plant.length(); i++){
            if (plant.charAt(i) == ',')
            {
                yloc = topLawn + ((spotHeight)*(Integer.parseInt(plant.substring(5, i))-1));
                xloc = leftLawn + (spotWidth*(Integer.parseInt(plant.substring(i+9))-1));
            }
        }
        int[] location = new int[2];
        location[0] = yloc;
        location[1] = xloc;
        for (Peashooter pea: peashooters){
            if (Arrays.equals(pea.location, location)){
                peashooters.remove(pea);
                break;
            }
        }
        for (Sunflower sun: sunflowers){
            if (Arrays.equals(sun.location, location)){
                sunflowers.remove(sun);
                break;
            }
        }
        for (Nut wall: wallnuts){
            if (Arrays.equals(wall.location, location)){
                wallnuts.remove(wall);
                break;
            }
        }
        for (Stump stum: stumps){
            if (Arrays.equals(stum.location, location)){
                stumps.remove(stum);
                break;
            }
        }
        for (Spike spi: spikes){
            if (Arrays.equals(spi.location, location)){
                spikes.remove(spi);
                break;
            }
        }
        for (int[] loc: occupiedLocations){
            if (Arrays.equals(loc, location)){
                occupiedLocations.remove(loc);
                indy = 1;
                break;
            }
        }
        if (indy == 1) {
        	int i = 0;
            for (Zombie zom : zombies){
                if ((zom.xlocation - zom.speed) < location[1] && (zom.xlocation + zom.speed) > location[1] && (zom.ylocation+(int)(0.1*spotHeight)) == location[0]){
                    zom.move();
                    zom.xlocation += (i*5);
                    i++;
                }
            }
        }
        
    }
    public void Planting(String plant){
        int xloc = 0;
        int yloc = 0;
        for (int i = 5; i<plant.length(); i++){
            if (plant.charAt(i) == ',')
            {
                yloc = topLawn + (spotHeight*(Integer.parseInt(plant.substring(5, i))-1));
                xloc = leftLawn + (spotWidth*(Integer.parseInt(plant.substring(i+9))-1));
            }
        }
        int[] location = new int[2];
        location[0] = yloc;
        location[1] = xloc;
        boolean contained = true;
        for (int[] a : occupiedLocations){
            contained = contained && !(Arrays.equals(a, location));
        }
        switch(chosenPlant){
            case "Peashooter":
                if(contained){
                    Peashooter ps = new Peashooter(60, location, imageHashMap.get(chosenPlant));
                    peashooters.add(ps);
                    occupiedLocations.add(location);
                    money = money - chosenCost;
                }
                break;
            case "Sunflower":
                if(contained){
                    Sunflower sn = new Sunflower(60, location, imageHashMap.get(chosenPlant));
                    sunflowers.add(sn);
                    occupiedLocations.add(location);
                    money = money - chosenCost;
                }
                break;
            case "Spikeweed":
                if(contained){
                    Spike sk = new Spike(100, location, imageHashMap.get(chosenPlant));
                    spikes.add(sk);
                    occupiedLocations.add(location);
                    money = money - chosenCost;
                }
                break;
            case "Torchwood":
                if(contained){
                    Stump st = new Stump(60, location, imageHashMap.get(chosenPlant));
                    stumps.add(st);
                    occupiedLocations.add(location);
                    money = money - chosenCost;
                }
                break;
            case "Wallnut":
                if(contained){
                    Nut nt = new Nut(720, location, imageHashMap.get("Wallnut_Full"), imageHashMap.get("Wallnut_Dented"), imageHashMap.get("Wallnut_Cracked"));
                    wallnuts.add(nt);
                    occupiedLocations.add(location);
                    money = money - chosenCost;
                }
                break;
            case "Spikerock":
                for (Spike sp : spikes){
                    if (Arrays.equals(sp.location, location) && (sp.state != 1)){
                        money = money - chosenCost;
                        sp.upgrade();
                        sp.Pic = imageHashMap.get("Spikerock");
                    }
                }
                break;
            }
        }
    MouseAdapter mouseLtnr = new MouseAdapter(){
    @Override
    public void mouseReleased(MouseEvent m) {
        // TODO Auto-generated method stub
        processMouse(m.getX(), m.getY());
        suncollectindicator = true;
    }
 };
 public void processMouse(int x, int y){
    Sun remove = null;
        for (Sun s : sunlight){
            if ((s.xlocation - s.width) <= x && (s.xlocation + (2*s.width)) >= x && (s.ylocation-s.height) <= y && (s.ylocation + (2*s.height)) >= y)
            {
                money += 25;
                remove = s;
                suncollectindicator = false;
            }
        }
        sunlight.remove(remove);
 }
 public void naturalSun(){
    int x_loc = (int)(Math.random()*screenSize.width);
    Sun newsun = new Sun(x_loc, 0, imageHashMap.get("Sun"), (-1*vertSpeed), spotWidth, spotWidth);
    sunRefresh = (int)(Math.random()*400) + 800;
    sunlight.add(newsun);
 }
 public void zombSpawn(){
     int lane = (int)(Math.random()*4)+1;
     int yloca = topLawn + (spotHeight*(lane-1)) - (int)(0.1*spotHeight);
     Zombie freshMeat = new Zombie(screenSize.width, yloca, Math.max((int)(horiSpeed*0.1*zombSpeedModifier), 1), zombHealthModifier);
     numZombs--;
     if (numZombs == 0){
        currWave++;
        zombSpeedModifier++;
        zombHealthModifier++;
        if (currWave == 4 && gameWon == 0 && gameLost == 0){
            gameWon = 1;
            gameEndWin();
        }
        else{
            Zombdelay /= 2;
            numZombs = 5*currWave;
            Zombrefresh = 1000;
        }
     }
     Zombrefresh += (Math.random()*Zombdelay)+200;
     zombies.add(freshMeat);
 }
 public void gameEndWin(){
        JOptionPane.showMessageDialog(this, "Good job\nYou successfully defeated an army of zombies.");
        try 
        {
            Thread.sleep(500);
        }
        catch (Exception ex) {}
        System.exit(0);
    }
    public void gameEndLose(){
        JOptionPane.showMessageDialog(this, "Too bad\nThe zombies ate your brainz.");
        try 
        {
            Thread.sleep(500);
        }
        catch (Exception ex) {}
        System.exit(0);
    }
}