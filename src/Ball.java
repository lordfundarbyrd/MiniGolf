import processing.core.PApplet;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.dist;
import processing.core.PVector;

public class Ball {
    
    float angle;
    float initVel;

    PVector pos; // position vector
    PVector vel; // velocity vector
    PVector acc; // acceleration vector
    PVector fr; // friction vector
    PVector fnet; // total force vector
    PVector norm; // normalized velocity vector
    PVector distNorm; // normalized distance vector
    PVector distHill; // distance to hill
    float holeDist;

    PApplet parent;

    boolean goal; // if the ball went in the hole
    boolean stopped; // if the ball stopped moving

    float dT;
    float d; // diameter of ball
    float r; // radius of ball
    float t; // time
    float m; // mass of ball
    float dist; 
    float hit;

    float rotateAngle;
    
    public Ball(PApplet pa, int x, int y, float ang, float speed) {
        parent = pa;
        initVel = 100*speed;
        d = 10;
        r = d / 2;
        m = 0.04575F;
        angle = ang;
        pos = new PVector(x, y);
        vel = new PVector(initVel * PApplet.cos(PApplet.radians(angle)), initVel * PApplet.sin(PApplet.radians(angle)));
        acc = new PVector();
        fnet = new PVector();
        fr = new PVector();
        dT = .01F;
        norm = new PVector();
        rotateAngle = angle;
        distNorm = new PVector();
        distHill = new PVector();
    }
    
    public void render() {
        parent.frameRate(60);
        parent.stroke(0);
        parent.strokeWeight(1);
        parent.fill(255);
        parent.ellipse(pos.x, parent.height - pos.y, d, d);
    }
    
    public void update() {
        holeDist = dist(pos.x,pos.y,500,250);
        
        fnet.set(0,0);
        if (goal == false && stopped == false) { // if balls are still moving
            
            hill();
            
            norm = PVector.div(vel,vel.mag()); // normalizes velocity
            fr = PVector.mult(norm,-1); // friction force
            fnet.add(fr); // add friction to total force
            acc=PVector.div(fnet, m);
            vel.add(PVector.mult(acc, dT));
            pos.add(PVector.mult(vel, dT));
            t += dT; // increase time
            
            goal();
        }
    }
    
        public void goal() {
        if ((abs((dist(pos.x, pos.y, parent.width - 500, parent.height - 250)))) <= 10) {
            goal = true;
            vel.set(0,0); // stop moving ball when it hits the goal
        }
        if (vel.mag() < .15F) {
            stopped = true;
            vel.set(0,0); // stop the ball when ball is barely moving
        }
        dist = abs((dist(pos.x, pos.y, parent.width - 500, parent.height - 250))); // calculates distance from hole
    }
        
        public void hill() {
            for (Hill h: Golf.hills) {
                PVector.sub(h.loc,pos,distHill);
                PVector.div(distHill,distHill.mag(),distNorm);
                if (37.5F < distHill.mag() && distHill.mag() < 75F) { // outside of hill/valley
                    //System.out.println(distHill + " outside");
                    if (h.up == true) { // if a hill
                        distNorm.mult(-500F/distHill.mag());
                        fnet.set(distNorm);
                    }
                    if (h.up == false) { // if a valley
                        distNorm.mult(500F/distHill.mag());
                        fnet.set(distNorm);
                    }
                }
                if (distHill.mag() < 37.5F) { // inside of hill/valley
                    if (h.up == true) { // if a hill
                        distNorm.mult(distHill.mag()*-.35F);
                        fnet.set(distNorm);
                    }
                    if (h.up == false) { // if a valley
                        distNorm.mult(distHill.mag()*.35F);
                        fnet.set(distNorm);
                    }
                }
                distNorm.set(0,0);
            }
        }
    
}
