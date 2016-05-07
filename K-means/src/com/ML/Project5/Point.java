/**
 * @author Pavan Kulkarni ,net-id :pxk142330
   Pojo class for point object
 */
package com.ML.Project5;

public class Point {

    private int id;
    private double x;
    private double y;
    private int cluster;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return 32 ^ (int) x ^ (int) y;
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = false;
        Point p = (Point) obj;
        if (Double.valueOf(p.x).equals(Double.valueOf(x)) && Double.valueOf(p.y).equals(Double.valueOf(y))) {
            flag = true;
        }
        return flag;
    }

    @Override
    public String toString() {
        return "Point [id=" + id + ", x=" + x + ", y=" + y + ", cluster=" + cluster + "]";
    }

}
