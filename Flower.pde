class Flower {
  PVector pos;
  float size = cellSize;
  int type; //born = 
  int age;
  color c;
  int alpha;
  float angle;
  boolean alive, watered;

  public Flower(float xPos, float yPos, color c) {
    this.pos = new PVector(xPos, yPos);
    this.c = c;
    this.type = 0;
    this.age = 0;
    this.angle = 0;
    this.alive = true;
    this.watered = false;
  }

  void update() {
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

  void setAlpha(int _a) {
    int a = (this.c >> 24) & 0xFF;
    int r = (this.c >> 16) & 0xFF;  // Faster way of getting red(argb)
    int g = (this.c >> 8) & 0xFF;   // Faster way of getting green(argb)
    int b = this.c & 0xFF;          // Faster way of getting blue(argb)
    this.c = color(r, g, b, this.alpha + _a);
  }

  void eat() {
    this.alive = false;
  }

  void render() {
    if (this.alive) {
      fill(this.c);
      pushMatrix();
      translate((width-height)/2, 0);
      translate((pos.x+1) * cellSize - cellSize/2, (pos.y+1) * cellSize - cellSize/2);
      //rotate(this.angle);
      if (this.age % 60 > 30)
        this.angle += 0.01;
      else
        this.angle -= 0.01;

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