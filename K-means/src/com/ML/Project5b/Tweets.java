/**
 * @author Pavan Kulkarni ,net-id :pxk142330
 */
package com.ML.Project5b;

/*
 * Pojo class to hold tweet objects
 */
public class Tweets {

    private String text;
    private long id;
    private int clusterId;
    private long centroidId;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(final int clusterId) {
        this.clusterId = clusterId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getCentroidId() {
        return centroidId;
    }

    public void setCentroidId(final long centroidId) {
        this.centroidId = centroidId;
    }

    @Override
    public String toString() {
        return id + "," + clusterId + "," + centroidId + "," + text;
    }

}
