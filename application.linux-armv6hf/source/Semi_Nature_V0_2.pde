import g4p_controls.*;
import net.java.games.input.*;
import org.gamecontrolplus.*;
import org.gamecontrolplus.gui.*;

int cell = 32;
int cellSize;

PFont loma;
PImage bg;

Player p1;
Player p2;
ArrayList<Racoon> racoons = new ArrayList<Racoon>();
ArrayList<Flower> flowers = new ArrayList<Flower>();
color[][] c = new color[cell][cell];

int maxTime = 150;
int timeLeft = maxTime;
int time;
int newGameTime = 0;
int endTime = 0;

color[] seedColor = new color[3];
color currentSeed = 0;
int seedNum = 0;
int sAlpha = 127;


ControlIO control;

void setup() {
  size(512, 512);
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
  if (control.getDevices().size() > 0) {
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

  seedColor[0] = color(#F2DF02, sAlpha);
  seedColor[1] = color(#027EE0, sAlpha);
  seedColor[2] = color(#F94CFA, sAlpha);
  currentSeed = seedColor[2];
  rectMode(CENTER);
}

void draw() {
  time = int(millis()/1000);
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

void GUI() {
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
  fill(#80D530);
  textSize(24);
  text(timeLeft, cellSize * 16, cellSize * 2);
}

void updateGarden() {
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


void drawGarden() {
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

void fillArray() {
  for (int j = 0; j < cell; j++) {
    for (int i = 0; i < cell; i++) {
      c[j][i] = color(0, random(150, 170), 10);
    }
  }
}