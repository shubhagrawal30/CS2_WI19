package edu.caltech.cs2.project01;
/*
import edu.caltech.cs2.libraries.FakeBody;
*/
import edu.caltech.cs2.libraries.IBody;
import edu.caltech.cs2.libraries.StdDraw;
import edu.caltech.cs2.libraries.Vector2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class NBodySimulation {

    public static IBody[] readBodies(Scanner in, int numBodies){
        IBody[] bodies = new Body[numBodies];
        for (int i = 0; i < numBodies; i++){
            bodies[i] = (new Body(in.nextDouble(), in.nextDouble(), in.nextDouble(),
                    in.nextDouble(), in.nextDouble(), in.next()));
        }
        return bodies;
    }

    public static void calculateStep(IBody[] bodies, double dt) {
        for (IBody body : bodies)
            body.calculateNewForceFrom(bodies);
        for (IBody body : bodies)
            body.updatePosition(dt);
    }

    public static void setupDrawing(double radius){
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-radius, radius);
    }

    public static void drawStep(IBody[] bodies){
        StdDraw.picture(0, 0,
                "data/images/starfield.jpg");
        for (IBody body : bodies){
            Vector2D position = body.getCurrentPosition();
            StdDraw.picture(position.getX(), position.getY(),
                    "data/images/" + body.getFileName());
        }
        StdDraw.show();
    }

    public static void printState(double radius, IBody[] bodies){
        System.out.println(bodies.length);
        System.out.printf("%5.2e%n", radius);
        for (IBody body : bodies)
            System.out.println(body);
    }

    public static void main(String[] args) throws FileNotFoundException {
        double dt = Double.parseDouble(args[1]);
        double T = Double.parseDouble(args[0]);
        Scanner in = new Scanner(new File(args[2]));
        int numBodies = (int)(in.nextDouble());
        double radius = in.nextDouble();
        IBody[] bodies = readBodies(in, numBodies);
        setupDrawing(radius);
        for (double time = 0; time < T; time += dt){
            drawStep(bodies);
            calculateStep(bodies, dt);
            StdDraw.pause(0);
        }
        printState(radius, bodies);
    }
}