
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import processing.core.PApplet;
import processing.core.PVector;

public class Golf extends PApplet {

    //variables for wall detection
    PVector wallDiff = new PVector();
    PVector cross = new PVector();
    PVector norm = new PVector();
    float dot;
    
    //variables for genetic algorithm
    float abeg = 0; // beginning angle
    float aend = 180; // ending angle
    float sbeg = 1; // beginning speed
    float send = 6; // ending speed    
    
    float size = 20; // size of generation
    int gen = 1;

    //variables for best ball
    float walls = 100; // least amt of walls hit
    float angle; // best angle
    float speed; // best speed
    float index; // index of best ball

    boolean last; // if last iteration

    public static List<Wall> borders = new ArrayList<>(); // walls arraylist
    public static List<Ball> balls = new ArrayList<>(); // balls arraylist
    public static List<Ball> bestBalls = new ArrayList<>(); // arraylist of balls closest to hole
    public static List<Hill> hills = new ArrayList<>(); // hills arraylist

    public static void main(String[] args) {
        PApplet.main(new String[]{Golf.class.getName()});
    }

    public void settings() {
        size(1000, 1000);
    }

    public void setup() {
        // creates walls
        borders.add(new Wall(this, 350, height - 200, width - 350, height - 200));
        borders.add(new Wall(this, 350, 200, 350, height - 200));
        borders.add(new Wall(this, width - 350, 200, width - 350, height - 200));
        borders.add(new Wall(this, 350, 200, width - 350, 200));

        // creates hills
        hills.add(new Hill(this, 550, 600, 75, false));
        hills.add(new Hill(this, 450, 400, 75, true));

        balls();
    }

    public void balls() {
        if (abeg != aend || sbeg != send) {
            for (int i = 0; i < size; i++) {
                balls.add(new Ball(this, width / 2, 220, (float) ThreadLocalRandom.current().nextDouble(abeg, aend), (float) ThreadLocalRandom.current().nextDouble(sbeg, send)));
            }
        }
        if (abeg == aend && sbeg == send) {
            last = true;
            for (int i = 0; i < size; i++) {
                balls.add(new Ball(this, width / 2, 220, (float) abeg, sbeg));
            }
        }
    }

    public void draw() {
        background(0, 255, 0);

        fill(0);
        noStroke();
        ellipse(500, 250, 25, 25); // hole

        fill(255, 0, 0);
        noStroke();
        rect(width / 2 - 20, height - 240, 40, 40); // starting spot

        for (Hill h : hills) {
            h.render(); // draw hills
        }

        for (int i = 0; i < balls.size(); i++) { // update and render balls
            balls.get(i).update();
            balls.get(i).render();
            reset();
        }

        for (Wall w : borders) {
            w.render(); // draw walls
            detect(w); // detect if balls hit it
        }

        fill(0);
        textSize(30);
        text("Best angle: " + angle, width / 2, 50);
        text("Best speed: " + speed/100, width / 2, 100);
        text("Generation " + gen, width / 2, 150);

        if (last == true) { // if last iteration
            if (ended() == true) { // if all balls are done
                noLoop(); // stop
            }
        }
    }

    // if all balls are either in the hole or not moving, ended is true
    public boolean ended() {
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).goal == false && balls.get(i).stopped == false) {
                return false;
            }
        }
        return true;
    }

    public boolean detect(Wall w) {
        for (Ball ball : balls) {
            if (dist(w.point2.x,w.point2.y,ball.pos.x,ball.pos.y)+dist(w.point1.x,w.point1.y,ball.pos.x,ball.pos.y)-dist(w.point2.x,w.point2.y,w.point1.x,w.point1.y)<=1) {
                wallDiff = PVector.sub(w.point2,w.point1);
                cross = wallDiff.cross(new PVector(0,0,-1));
                norm = cross.normalize();
                dot = PVector.dot(ball.vel,norm);
                norm.mult(dot);
                ball.vel.add(norm.mult(-2));
                ball.pos.add(PVector.mult(ball.vel, ball.dT));
                return true; 
            }
        }
        return false;
    }

    public void holeSort(List<Ball> b) {
        for (int i = 0; i < b.size(); i++) {
            for (int j = b.size() - 1; j > i; j--) {
                if (b.get(i).holeDist > b.get(j).holeDist) {
                    Ball tmp = b.get(i);
                    b.set(i, b.get(j));
                    b.set(j, tmp);
                }
            }
        }
    }
    
    public void wallSort(List<Ball> b) {
        for (int i = 0; i < b.size(); i++) {
            for (int j = b.size() - 1; j > i; j--) {
                if (b.get(i).hit > b.get(j).hit) {
                    Ball tmp = b.get(i);
                    b.set(i, b.get(j));
                    b.set(j, tmp);
                }
            }
        }
    }

    public void best() {
        for (int i = 0; i < balls.size()*.5F; i++) {
            bestBalls.add(balls.get(i));
        }
        wallSort(bestBalls);
        for (Ball b : bestBalls) {
            if (b.hit < walls) {
                walls = b.hit;
                angle = b.angle;
                speed = b.initVel;
            }
        }
    }

    public void setRange() {
        abeg = bestBalls.get(bestBalls.size()-1).angle;
        aend = bestBalls.get((int) (bestBalls.size()*.9F)).angle;
        if (abeg > aend) {
            float a = abeg;
            abeg = aend;
            aend = a;
        }
        
        sbeg = bestBalls.get(bestBalls.size()-1).initVel/100;
        send = bestBalls.get((int) (bestBalls.size()*.9F)).initVel/100;
        if (sbeg > send) {
            float s = sbeg;
            sbeg = send;
            send = s;
        }
    }

    public void reset() {
        if (ended() == true) { // if all balls are done, sort array and find best ball
            holeSort(balls);
            best();
            if (abeg != aend || sbeg != send) {
                setRange();
            }
                balls.clear();
                bestBalls.clear();
            gen++;
            balls(); // generate new balls with new ranges
        }
    }
}
