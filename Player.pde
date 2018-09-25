final int arroseTime = 20;
final int arroseMaxUse = 10;
final int seedMaxUse = 24;

int restartTime = 0;

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
    this.pos = new PVector(16 + int(random(-4, 4)), 16 + int(random(-4, 4)));
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

  void render() {
    fill(255, 0, 0);
    pushMatrix();
    translate((width-height)/2, 0);
    translate(pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2);
    rotate(this.angle);
    if (pImg != null)
      image(pImg, 0, 0);
    else
      rect(0, 0, cellSize, cellSize);
    popMatrix();
  }

  void updatePlayer() {
    if (this.cont != null) {
      softReset();
      if (frameCount % 7 == 0)
        control();
      if (frameCount % 100 == 0 && this.toolCurrent < this.toolCapacity) {
        if (this.tool.equals("Plante"))
          this.toolCurrent += int(random(3, 6));
        else
          this.toolCurrent ++;
      }
      useTool(this.tool);
    }
  }

  void useTool(String tool) {
    if (tool.equals("Arrose")) {
      water();
    } 

    if (tool.equals("Plante")) {
      if (frameCount % 7 == 0)
        seed();
    }
  }

  void control() {
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
    if (this.cont.getSlider(1).getValue() < -0.7)
      this.pos.y += -1;
    if (this.cont.getSlider(1).getValue() > 0.7)
      this.pos.y += 1;
    if (this.cont.getSlider(0).getValue() < -0.7)
      this.pos.x += -1;
    if (this.cont.getSlider(0).getValue() > 0.7)
      this.pos.x += 1;

    this.pos.x = constrain(this.pos.x, 0, cell-1);
    this.pos.y = constrain(this.pos.y, 0, cell-1);

    if (!lPos.equals(this.pos)) {
      this.direction = lPos.sub(pos);
      lPos.mult(-1);
      //println(this.direction);
    }
  }

  void seed() {
    if (actionA() && this.toolCurrent > 0) {
      boolean o = false;
      for (Flower f : flowers)
        if (this.pos.dist(f.pos) == 0)
          o = true;
      if (!o) {
        flowers.add(new Flower(this.pos.x, this.pos.y, currentSeed));
        this.toolCurrent--;
      }
    }
    if (actionB())
      switchSeed();
  }

  void switchSeed() {
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

  void water() {
    if ((actionA() || actionB()) && this.toolTime == 0 && this.toolCurrent > 0) {
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
  
  boolean actionA() {
   if (this.cont.getButton(1).pressed() || this.cont.getButton(2).pressed() || this.cont.getButton(4).pressed())
     return(true);
   else
     return(false);
  }
  
  boolean actionB() {
   if (this.cont.getButton(0).pressed() || this.cont.getButton(3).pressed() || this.cont.getButton(5).pressed())
     return(true);
   else
     return(false);
  }
  
  int softResetTimer = 0;
  void softReset() {
    
    if (this.cont.getButton(8).pressed())
      softResetTimer++;
    if (softResetTimer == 240) {
      println("Soft Reset -> New Game");
      resetGame();
      softResetTimer = 0; 
    }
  }

  void drawWater() {
    pushMatrix();
    translate((width-height)/2, 0);
    fill(0, 0, 255, 255/arroseMaxUse * this.toolCurrent);
    pushMatrix();
    this.angle = TWO_PI/arroseTime*(toolTime-1);
    translate(this.pos.x * cellSize, this.pos.y * cellSize);
    translate(cellSize/2, cellSize/2);
    rotate(this.angle);
    rect(-cellSize * 1.5, 0, cellSize/2, cellSize/2);
    rect(cellSize * 1.5, 0, cellSize/2, cellSize/2);
    rect(0, -cellSize * 1.5, cellSize/2, cellSize/2);
    rect(0, cellSize * 1.5, cellSize/2, cellSize/2);
    popMatrix();
    popMatrix();
    this.toolTime--;
  }
}