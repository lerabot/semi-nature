boolean inFrame(PVector pos) {
  if (pos.x > width/cellSize || pos.x < 0 || pos.y > height/cellSize || pos.y < 0)
    return false;
  else
    return true;
}

boolean flash = true;

void endGame() {
  int resetTime = 350;
  if (noButtons())
    endTime++;

  textAlign(CENTER);

  textSize(18);
  fill(0);
  text(flowers.size() + " fleurs plantées!", width/2, height-80);
  fill(#027EE0);
  text(flowers.size() + " fleurs plantées!", width/2+1, height-80+1);

  textSize(24);
  fill(0);
  text("Fin de la partie", width/2, height-50);
  fill(#027EE0);
  text("Fin de la partie", width/2+1, height-50+1);

  if (frameCount % 40 == 0)
    flash = !flash;

  if (flash && endTime > resetTime) {
    textSize(18);
    fill(0);
    text("Appuyer sur Action pour recommencer", width/2, height-30);
    fill(#027EE0);
    text("Appuyer sur Action pour recommencer", width/2+1, height-30+1);
  }

  if ((p1.cont.getButton(0).pressed() || p2.cont.getButton(0).pressed()) && endTime > resetTime) {
    resetGame();
  }
}

void resetGame() {
  endTime = 0;
  timeLeft = maxTime;
  newGameTime = time;
  p1.pos = new PVector(int(random(32)), int(random(32)));
  p2.pos = new PVector(int(random(32)), int(random(32)));

  p1.toolCurrent = arroseMaxUse;
  p2.toolCurrent = seedMaxUse;
  flowers.clear();
  racoons.clear();

  println(time);
  println(timeLeft);
}

boolean noButtons() {
  if (p1.cont.getButton(0).pressed() || p2.cont.getButton(0).pressed())
    return false;
  else
    return true;
}