import processing.core.PApplet;
import processing.core.PVector;

public class Wall extends Golf {
    
    PApplet parent; 
    PVector point1;  // endpoint 1
    PVector point2; // endpoint 2
    float m; // slope of line
    float wallAngle; // angle wall is at
    
    public Wall(PApplet p, int x1, int y1, int x2, int y2) {
        parent = p;
        point1 = new PVector (x1,y1);
        point2 = new PVector (x2,y2);
        m = (point2.y-point1.y)/(point2.x-point1.x);
        wallAngle = 180-PVector.angleBetween(point1,point2)-(90-PVector.angleBetween(point1,point2)); // if wall is diagonal this is needed
    }
    
    public void render() {
        parent.strokeWeight(5);
        parent.stroke(139,69,19);
        parent.line(point1.x,point1.y,point2.x,point2.y); // draws line with endpoints
    }
}
