import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import net.java.games.input.*; 
import org.gamecontrolplus.*; 
import org.gamecontrolplus.gui.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Semi_Nature_V0_2 extends PApplet {






int cell = 32;
int cellSize;

PFont loma;
PImage bg;

Player p1;
Player p2;
ArrayList<Racoon> racoons = new ArrayList<Racoon>();
ArrayList<Flower> flowers = new ArrayList<Flower>();
int[][] c = new int[cell][cell];

int maxTime = 150;
int timeLeft = maxTime;
int time;
int newGameTime = 0;
int endTime = 0;

int[] seedColor = new int[3];
int currentSeed = 0;
int seedNum = 0;
int sAlpha = 127;


ControlIO control;

public void setup() {
  
  noCursor();
  //fullScreen();
  //frameRate(24);
  noStroke();
  //noSmooth();

  loma = loadFont("loma-font.vlw");
  textFont(loma);

  bg = loadImage("bg_v3.png");

  control = ControlIO.getInstance(this);
  String listing = control.deviceListToText("");
  println(listing);
  if (control.getDevices().size() > 0) {
    p1 = new Player(control.getDevice(4), 0, "Arrose");
    p2 = new Player(control.getDevice(0), 1, "Plante");
  } else {
    p1 = new Player(null, 0, "Arrose");
    p2 = new Player(null, 1, "Plante");
  }

  racoons.add(new Racoon());
  racoons.add(new Racoon());
  racoons.add(new Racoon());
  println(racoons.size());

  cellSize = width/cell;
  fillArray();

  seedColor[0] = color(0xffF2DF02, sAlpha);
  seedColor[1] = color(0xff027EE0, sAlpha);
  seedColor[2] = color(0xffF94CFA, sAlpha);
  currentSeed = seedColor[2];
  rectMode(CENTER);
}

public void draw() {
  time = PApplet.parseInt(millis()/1000);
  //background(35);

  updateGarden();
  drawGarden();



  timeLeft = constrain((newGameTime + maxTime) - time, 0, maxTime);
  if (timeLeft == 0)
    endGame();
  else {
    for (int i = racoons.size()-1; i > 0; i--) {
      racoons.get(i).update();
      racoons.get(i).render();
    }
    p1.updatePlayer();
    p2.updatePlayer();
  }
  p2.render();
  p1.render();


  GUI();
}

public void GUI() {
  int o = -3;
  /*
  fill(0);
   textAlign(LEFT);
   text("Joueur1", cellSize * 1 - o, cellSize * 30 + o);
   text("" + p1.tool + " " + p1.toolCurrent, cellSize * 1 - o, cellSize * 31 + o);
   
   textAlign(RIGHT);
   fill(0);
   textSize(14);
   text(flowers.size() + " fleurs", cellSize * 30 - o, cellSize * 30 + o);
   */

  /////////////////TOOLS
  int offset = 20;
  fill(0);
  textSize(12);
  text(p1.toolCurrent, cellSize * p1.pos.x + offset, cellSize * p1.pos.y);
  text(p2.toolCurrent, cellSize * p2.pos.x + offset, cellSize * p2.pos.y);



  textAlign(CENTER);
  fill(0xff80D530);
  textSize(24);
  text(timeLeft, cellSize * 16, cellSize * 2);
}

public void updateGarden() {
  if (flowers.size() > 0) {
    for (int i = flowers.size() - 1; i > 0; i--) {
      Flower f = flowers.get(i);

      //briser les fleurs
      if (PVector.dist(p1.pos, f.pos) == 0 && f.age > 1000) 
        f.setAlpha(-50);

      if (f.alive == false)
        flowers.remove(i);

      f.update();
    }
  }

  if (frameCount % 1200 == 0)
    racoons.add(new Racoon());
}


public void drawGarden() {
  //colones
  imageMode(CENTER);
  image(bg, width/2, height/2);
  /*
  for (int j = 0; j < cell; j++) {
   //ligne
   for (int i = 0; i < cell; i++) {
   fill(c[j][i]);
   rect(i*cellSize + cellSize/2, j*cellSize+ cellSize/2, cellSize, cellSize);
   }
   }
   */

  //FLOWER DRAW LOOP
  if (flowers.size() > 0) {
    for (int i = flowers.size(); i > 0; i--) {
      //flowers.get(i-1).update();
      flowers.get(i-1).render();
    }
  }
}

public void fillArray() {
  for (int j = 0; j < cell; j++) {
    for (int i = 0; i < cell; i++) {
      c[j][i] = color(0, random(150, 170), 10);
    }
  }
}
class Flower {
  PVector pos;
  float size = cellSize;
  int type; //born = 
  int age;
  int c;
  int alpha;
  float angle;
  boolean alive, watered;

  public Flower(float xPos, float yPos, int c) {
    this.pos = new PVector(xPos, yPos);
    this.c = c;
    this.type = 0;
    this.age = 0;
    this.angle = 0;
    this.alive = true;
    this.watered = false;
  }

  public void update() {
    if (this.age % 300 == 0 && this.watered == true) {
      int a = (this.c >> 24) & 0xFF;
      int r = (this.c >> 16) & 0xFF;  // Faster way of getting red(argb)
      int g = (this.c >> 8) & 0xFF;   // Faster way of getting green(argb)
      int b = this.c & 0xFF;          // Faster way of getting blue(argb)
      if (a < 160)
        a += 20;
      this.c = color(r, g, b, a);
      this.alpha = a;
    }
    if (this.watered)
      this.age++;
  }

  public void setAlpha(int _a) {
    int a = (this.c >> 24) & 0xFF;
    int r = (this.c >> 16) & 0xFF;  // Faster way of getting red(argb)
    int g = (this.c >> 8) & 0xFF;   // Faster way of getting green(argb)
    int b = this.c & 0xFF;          // Faster way of getting blue(argb)
    this.c = color(r, g, b, this.alpha + _a);
  }

  public void eat() {
    this.alive = false;
  }

  public void render() {
    if (this.alive) {
      fill(this.c);
      pushMatrix();
      translate((pos.x+1) * cellSize - cellSize/2, (pos.y+1) * cellSize - cellSize/2);
      //rotate(this.angle);
      if (this.age % 60 > 30)
        this.angle += 0.01f;
      else
        this.angle -= 0.01f;

      if (this.age < 1000)
        rect(0, 0, size/2, size/2);
      else if (this.age < 5000 && this.age > 1000)
        rect(0, 0, size, size);
      if (this.age > 2000) {
        rect(0, 0, size, size);
        //rect(0 +16, 0, size/2, size/2);
        //rect(0, 0 -16, size/2, size/2);
        //rect(0, 0 +16, size/2, size/2);
      }
      popMatrix();
    }
  }
}
final int arroseTime = 20;
final int arroseMaxUse = 10;
final int seedMaxUse = 24;

class Player {
  PVector pos;
  PVector direction;
  String tool;
  int toolTime; // 20 frames %
  int toolCapacity;
  int toolCurrent;
  int playerNum;
  ControlDevice cont;
  String hatNum;
  PImage pImg;
  float angle;

  public Player(ControlDevice cont, int playerNumber, String tool) {
    this.pos = new PVector(PApplet.parseInt(random(32)), PApplet.parseInt(random(32)));
    this.direction  = this.pos;
    this.tool = tool;
    this.playerNum = playerNumber;
    this.cont = cont;
    this.hatNum = "pov";
    this.toolTime = 0;
    if (tool.equals("Arrose")) {
      pImg = loadImage("arro.png");
      this.toolCapacity = this.toolCurrent = arroseMaxUse;
    }
    if (tool.equals("Plante")) {
      pImg = loadImage("mainMauve.png");
      this.toolCapacity = this.toolCurrent = seedMaxUse;
    }
  }

  public void render() {
    fill(255, 0, 0);
    pushMatrix();
    translate(pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2);
    rotate(this.angle);
    if (pImg != null)
      image(pImg, 0, 0);
    else
      rect(0, 0, cellSize, cellSize);
    popMatrix();
  }

  public void updatePlayer() {
    if (this.cont != null) {
      if (frameCount % 7 == 0)
        control();
      if (frameCount % 100 == 0 && this.toolCurrent < this.toolCapacity) {
        if (this.tool.equals("Plante"))
          this.toolCurrent += PApplet.parseInt(random(3, 6));
        else
          this.toolCurrent ++;
      }
      useTool(this.tool);
    }
  }

  public void useTool(String tool) {
    if (tool.equals("Arrose")) {
      water();
    } 

    if (tool.equals("Plante")) {
      if (frameCount % 7 == 0)
        seed();
    }
  }

  public void control() {
    PVector lPos = new PVector(pos.x, pos.y);
    /*
    if (this.cont.getHat(this.hatNum).up()     || this.cont.getSlider(1).getValue() < -0.7)
     this.pos.y += -1;
     if (this.cont.getHat(this.hatNum).down()   || this.cont.getSlider(1).getValue() > 0.7)
     this.pos.y += 1;
     if (this.cont.getHat(this.hatNum).left()   || this.cont.getSlider(0).getValue() < -0.7)
     this.pos.x += -1;
     if (this.cont.getHat(this.hatNum).right()  || this.cont.getSlider(0).getValue() > 0.7)
     this.pos.x += 1;
     */
    if (this.cont.getSlider(1).getValue() < -0.7f)
      this.pos.y += -1;
    if (this.cont.getSlider(1).getValue() > 0.7f)
      this.pos.y += 1;
    if (this.cont.getSlider(0).getValue() < -0.7f)
      this.pos.x += -1;
    if (this.cont.getSlider(0).getValue() > 0.7f)
      this.pos.x += 1;

    if (!lPos.equals(this.pos)) {
      this.direction = lPos.sub(pos);
      lPos.mult(-1);
      //println(this.direction);
    }
  }

  public void seed() {
    if (this.cont.getButton(0).pressed() && this.toolCurrent > 0) {
      boolean o = false;
      for (Flower f : flowers)
        if (this.pos.dist(f.pos) == 0)
          o = true;
      if (!o) {
        flowers.add(new Flower(this.pos.x, this.pos.y, currentSeed));
        this.toolCurrent--;
      }
    }
    if (this.cont.getButton(1).pressed())
      switchSeed();
  }

  public void switchSeed() {
    seedNum++;
    if (seedNum == 3)
      seedNum = 0;
    if (seedNum == 0)
      this.pImg = loadImage("mainJaune.png");
    if (seedNum == 1)
      this.pImg = loadImage("mainBleu.png");
    if (seedNum == 2)
      this.pImg = loadImage("mainMauve.png");

    currentSeed = seedColor[seedNum];
  }

  public void water() {
    if ((this.cont.getButton(1).pressed() || this.cont.getButton(0).pressed()) && this.toolTime == 0 && this.toolCurrent > 0) {
      this.toolTime = arroseTime;
      this.toolCurrent--;
      println("Player > " + this.pos);
    }
    if (this.toolTime > 0 && this.toolCurrent > 0) {
      drawWater();
      for (Racoon r : racoons) {
        if (this.pos.dist(r.pos) < 3 && r.shaken == false) {
          r.shake(this.direction);
          r.scareLevel = 15;
        }
      }

      for (Flower f : flowers) {
        if (this.pos.dist(f.pos) < 2 && !f.watered) {
          f.watered = true;
          println("Flower > " + f.pos);
        }
      }
    }
  }

  public void drawWater() {
    fill(0, 0, 255, 255/arroseMaxUse * this.toolCurrent);
    pushMatrix();
    this.angle = TWO_PI/arroseTime*(toolTime-1);
    translate(this.pos.x * cellSize, this.pos.y * cellSize);
    translate(cellSize/2, cellSize/2);
    rotate(this.angle);
    rect(-cellSize * 1.5f, 0, cellSize/2, cellSize/2);
    rect(cellSize * 1.5f, 0, cellSize/2, cellSize/2);
    rect(0, -cellSize * 1.5f, cellSize/2, cellSize/2);
    rect(0, cellSize * 1.5f, cellSize/2, cellSize/2);
    popMatrix();
    this.toolTime--;
  }
}
class Racoon {
  PVector pos;
  Flower target;
  int speed, hunger, flower, hideDelay, shakenTime;
  float scareLevel;
  boolean scared, shaken;
  PImage rImg; 

  public Racoon() {
    this.pos = new PVector(16,16);
    PVector r = PVector.random2D();
    r.setMag(24);
    r.x = PApplet.parseInt(r.x);
    r.y = PApplet.parseInt(r.y);
    this.pos.add(r);
    
    
    this.speed = PApplet.parseInt(random(40, 70));
    this.flower = 0;
    this.scareLevel = random(4, 7);
    this.scared = this.shaken = false;
    this.rImg = loadImage("data/r2.png");
  }

  public void render() {
    fill(0, 0, 0);
    image(this.rImg, pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2);
    //rect(pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2, cellSize, cellSize);
  }

  public void selectTarget() {
    float d = 100;
    for (Flower f : flowers) {
      if (f.pos.dist(this.pos) < d && f.alive == true) {
        d = f.pos.dist(this.pos);
        this.target = f;
      }
    }
  }

  public void followTarget(Flower target) {
    if (target != null) {
      if (frameCount % this.speed == 0) {
        PVector direction = new PVector();
        direction = PVector.sub(this.pos, target.pos);
        direction.normalize();
        direction.mult(-1);
        direction.x = round(direction.x);
        direction.y = round(direction.y);
        this.pos.add(direction);
      }

      if (this.pos.dist(target.pos) == 0 && !this.shaken && !this.scared)
        eatFlower();
    }
  }

  public void eatFlower() {
    target.eat();
    this.flower++;
    if (this.flower % 3 == 0)
      this.scareLevel = constrain(this.scareLevel--, 3, this.scareLevel);

    this.speed = constrain(this.speed--, 20, 70);
    selectTarget();
  }

  public void avoidPlayer() {
    if (frameCount % 15 == 0) {
      if (this.pos.dist(p1.pos) < this.scareLevel || this.pos.dist(p2.pos) < this.scareLevel) {
        PVector direction = new PVector();
        direction = PVector.sub(this.pos, p1.pos);
        direction.normalize();
        //direction.mult(-1);
        direction.x = round(direction.x);
        direction.y = round(direction.y);
        this.scared = true;
        this.speed = 25;
        this.pos.add(direction);
        if (random(1) > 0.93f)
          this.scareLevel++;
      } else {
        this.scared = false;
        this.speed = 45;
      }
    }
  }

  public void shake(PVector dir) {
    //this.pos.x += int(random(-5, 5));
    //this.pos.y += int(random(-5, 5));
    dir.mult(4);
    this.pos.add(dir);
    this.shaken = true;
    this.shakenTime = PApplet.parseInt(random(7, 20));
   }

  public void hide() {
    if (this.scared == true && !inFrame(this.pos)) {
      this.hideDelay = PApplet.parseInt(random(5, 15));
      this.scareLevel += PApplet.parseInt(random(2, 5));
    }

    if (this.hideDelay > 0 && frameCount % 60 == 0)
      this.hideDelay--;
  }

  public void update() {

    if (this.target == null || this.shaken || this.scared || random(1) > 0.985f)
      selectTarget(); 

    if (this.hideDelay == 0 || this.shaken || this.scared) {
      followTarget(this.target);
    }
    
    if (this.shaken && frameCount % 60 == 0)
      this.shakenTime--;
      
    if (this.shakenTime == 0) {
      this.shaken = false;
      this.scareLevel = PApplet.parseInt(random(2,6));
    }

    hide();
    avoidPlayer();
  }
}
public boolean inFrame(PVector pos) {
  if (pos.x > width/cellSize || pos.x < 0 || pos.y > height/cellSize || pos.y < 0)
    return false;
  else
    return true;
}

boolean flash = true;

public void endGame() {
  if (noButtons())
    endTime++;

  textAlign(CENTER);

  textSize(18);
  fill(0);
  text(flowers.size() + " fleurs plant\u00e9es!", width/2, height-80);
  fill(0xff027EE0);
  text(flowers.size() + " fleurs plant\u00e9es!", width/2+1, height-80+1);

  textSize(24);
  fill(0);
  text("Fin de la partie", width/2, height-50);
  fill(0xff027EE0);
  text("Fin de la partie", width/2+1, height-50+1);

  if (frameCount % 40 == 0)
    flash = !flash;

  if (flash && endTime > 200) {
    textSize(18);
    fill(0);
    text("Appuyer sur Action pour recommencer", width/2, height-30);
    fill(0xff027EE0);
    text("Appuyer sur Action pour recommencer", width/2+1, height-30+1);
  }

  if ((p1.cont.getButton(0).pressed() || p2.cont.getButton(0).pressed()) && endTime > 200) {
    resetGame();
    endTime = 0;
  }
}

public void resetGame() {
  endTime = 0;
  timeLeft = maxTime;
  newGameTime = time;
  p1.pos = new PVector(PApplet.parseInt(random(32)), PApplet.parseInt(random(32)));
  p2.pos = new PVector(PApplet.parseInt(random(32)), PApplet.parseInt(random(32)));

  p1.toolCurrent = arroseMaxUse;
  p2.toolCurrent = seedMaxUse;
  flowers.clear();
  racoons.clear();


  println(time);
  println(timeLeft);
}

public boolean noButtons() {
  if (p1.cont.getButton(0).pressed() || p2.cont.getButton(0).pressed())
    return false;
  else
    return true;
}
  public void settings() {  size(512, 512); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Semi_Nature_V0_2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
