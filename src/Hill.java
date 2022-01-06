
import processing.core.PApplet;
import processing.core.PVector;

public class Hill {
    
    PApplet parent;
    PVector loc; // location of hill/valley
    float r; // radius of hill/valley
    boolean up; // true if a hill, false if a valley
    
    public Hill (PApplet p, int x, int y, float rad, boolean t) {
        parent = p;
        loc = new PVector(x,y);
        r = rad;
        up = t;
    }
    
    public void render() {
        if (up == true) { // hill
            parent.fill(173,255,47);
        }
        if (up == false) { // valley
            parent.fill(50,205,50);
        }
        parent.noStroke();
        parent.ellipse(loc.x,parent.height-loc.y,2*r,2*r);
        parent.fill(255,0,0);
        parent.ellipse(loc.x,parent.height-loc.y,5,5); // center of hill
    }
}
