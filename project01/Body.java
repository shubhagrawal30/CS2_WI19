package edu.caltech.cs2.project01;

import edu.caltech.cs2.libraries.IBody;
import edu.caltech.cs2.libraries.Vector2D;

public class Body implements IBody<Body> {

  private Vector2D position, velocity;
  private final double mass;
  private final String filename;
  private Vector2D force;
  private final static double GRAV_CONSTANT = 6.67E-11;


  public Body(Vector2D position, Vector2D velocity, double mass, String filename){
    this.position = position;
    this.velocity = velocity;
    this.mass = mass;
    this.filename = filename;
    this.force = null;
  }

  public Body(double xPos, double yPos, double xVel, double yVel, double mass, String filename){
    this(new Vector2D(xPos, yPos), new Vector2D(xVel, yVel), mass, filename);
  }

  public Body(double xPos, double yPos, double xVel, double yVel){
    this(xPos, yPos, xVel, yVel, 0, null);
  }

  @Override
  public void calculateNewForceFrom(Body[] bodies) {
    Vector2D netForce = new Vector2D(0,0);
    for (Body body : bodies){
      if (this != body)
        netForce = netForce.add(this.getForceFrom(body));
    }
    this.force = netForce;
  }

  private double distanceTo(Body other){
    return Math.sqrt(Math.pow(this.position.getX() - other.position.getX(), 2) +
            Math.pow(this.position.getY() - other.position.getY(), 2));
  }

  private Vector2D getForceFrom(Body other) {
    double deltaX = other.position.getX() - this.position.getX();
    double deltaY = other.position.getY() - this.position.getY();
    double distance = this.distanceTo(other);
    double force = GRAV_CONSTANT * this.mass * other.mass / Math.pow(this.distanceTo(other), 2);
    return new Vector2D(deltaX * force / distance, deltaY * force / distance);
  }

  @Override
  public void updatePosition(double dt) {
    this.velocity = this.velocity.add(new Vector2D(dt * this.force.getX() / this.mass,
            dt * this.force.getY() / this.mass));
    this.position = this.position.add(new Vector2D(dt * this.velocity.getX(),
            dt * this.velocity.getY()));
  }

  public String toString(){
    return String.format("%11.4e %11.4e %11.4e %11.4e %11.4e %12s", position.getX(), position.getY(),
            velocity.getX(), velocity.getY(), this.mass, this.getFileName());
  }

  @Override
  public Vector2D getCurrentPosition() {
    return this.position;
  }

  @Override
  public String getFileName() {
    return this.filename;
  }
}