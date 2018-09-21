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
    r.x = int(r.x);
    r.y = int(r.y);
    this.pos.add(r);
    
    
    this.speed = int(random(40, 70));
    this.flower = 0;
    this.scareLevel = random(4, 7);
    this.scared = this.shaken = false;
    this.rImg = loadImage("data/r2.png");
  }

  void render() {
    fill(0, 0, 0);
    image(this.rImg, pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2);
    //rect(pos.x * cellSize + cellSize/2, pos.y * cellSize + cellSize/2, cellSize, cellSize);
  }

  void selectTarget() {
    float d = 100;
    for (Flower f : flowers) {
      if (f.pos.dist(this.pos) < d && f.alive == true) {
        d = f.pos.dist(this.pos);
        this.target = f;
      }
    }
  }

  void followTarget(Flower target) {
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

  void eatFlower() {
    target.eat();
    this.flower++;
    if (this.flower % 3 == 0)
      this.scareLevel = constrain(this.scareLevel--, 3, this.scareLevel);

    this.speed = constrain(this.speed--, 20, 70);
    selectTarget();
  }

  void avoidPlayer() {
    if (frameCount % 15 == 0) {
      if (this.pos.dist(p1.pos) < this.scareLevel || this.pos.dist(p2.pos) < this.scareLevel) {
        PVector direction = new PVector();
        direction = PVector.sub(this.pos, p1.pos);
        direction.normalize();
        //direction.mult(-1);
        direction.x = round(direction.x);
        direction.y = round(direction.y);
        this.scared = true;
        this.speed = 25;
        this.pos.add(direction);
        if (random(1) > 0.93)
          this.scareLevel++;
      } else {
        this.scared = false;
        this.speed = 45;
      }
    }
  }

  void shake(PVector dir) {
    //this.pos.x += int(random(-5, 5));
    //this.pos.y += int(random(-5, 5));
    dir.mult(4);
    this.pos.add(dir);
    this.shaken = true;
    this.shakenTime = int(random(7, 20));
   }

  void hide() {
    if (this.scared == true && !inFrame(this.pos)) {
      this.hideDelay = int(random(5, 15));
      this.scareLevel += int(random(2, 5));
    }

    if (this.hideDelay > 0 && frameCount % 60 == 0)
      this.hideDelay--;
  }

  void update() {

    if (this.target == null || this.shaken || this.scared || random(1) > 0.985)
      selectTarget(); 

    if (this.hideDelay == 0 || this.shaken || this.scared) {
      followTarget(this.target);
    }
    
    if (this.shaken && frameCount % 60 == 0)
      this.shakenTime--;
      
    if (this.shakenTime == 0) {
      this.shaken = false;
      this.scareLevel = int(random(2,6));
    }

    hide();
    avoidPlayer();
  }
}