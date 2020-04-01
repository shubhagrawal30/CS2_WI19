package edu.caltech.cs2.lab09;

public class Point {
    public int x; // x coordinate of point
    public int y; // y coordinate of point
    public Point parent; // Parent node of point (may be null)
    private Integer hash;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = null;
    }

    /*
     * Returns true if the point passed has the same x and y coordinate as
     * this point, or false otherwise.
     */
    public boolean isEqual(Point point) {
        if (point == null) return false;
        int x = point.x;
        int y = point.y;
        return (this.x == x && this.y == y);
    }

    /*
     * Returns true if the point passed has the same x and y coordinate as
     * this point, or false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point point = (Point) o;
        if (point == null) return false;
        int x = point.x;
        int y = point.y;
        return (this.x == x && this.y == y);
    }

    public int hashCode() {
        int hash = 11;
        hash = hash * 70 + this.x;
        hash = hash * 70 + this.y;
        return hash;
    }
}
