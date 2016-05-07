/**
 * @author Pavan Kulkarni ,net-id :pxk142330
 */

package com.ML.Project5b;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class KMeansJaccard {

    private int k;
    private List<Tweets> tweetList;
    private List<Tweets> centroidList;
    private Map<Long, Tweets> tweetMap;

    public List<Tweets> getCentroidList() {
        return centroidList;
    }

    public void setCentroidList(final List<Tweets> centroidList) {
        this.centroidList = centroidList;
    }

    public int getK() {
        return k;
    }

    public void setK(final int k) {
        this.k = k;
    }

    public List<Tweets> getTweetList() {
        return tweetList;
    }

    public void setTweetList(final List<Tweets> tweetList) {
        this.tweetList = tweetList;
    }

    public Map<Long, Tweets> getTweetMap() {
        return tweetMap;
    }

    public void setTweetMap(final Map<Long, Tweets> tweetMap) {
        this.tweetMap = tweetMap;
    }

    public KMeansJaccard(int k) {
        this.k = k;
        tweetList = new ArrayList<>();
        tweetMap = new HashMap<>();
        centroidList = new ArrayList<>();
    }

    /*
     * parse the file and make tweet objects out of tweets.json file
     */
    public void makeTweetObjects(final String inputFile) throws IOException {
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;

        while ((str = br.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(str);
            Tweets tweetAttributes = new Tweets();
            tweetAttributes.setText(jsonObject.getString("text"));
            long id = jsonObject.getLong("id");
            tweetAttributes.setId(id);

            tweetMap.put(id, tweetAttributes);
            tweetList.add(tweetAttributes);
        }
        br.close();
    }

    /*
     * get the initial centroids initialSeeds.txt file
     */
    public List<Tweets> getInitialCentroids(final String initailCentroidsFile) throws IOException {
        File file = new File(initailCentroidsFile);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        int count = 0;
        while ((str = br.readLine()) != null && count < this.k) {
            count++;
            if (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
            Tweets tweetAttributes = tweetMap.get(Long.valueOf(str));
            centroidList.add(tweetAttributes);
        }
        br.close();

        return centroidList;
    }

    /*
     * get new centroids after clustering
     */
    public List<Tweets> getNewCentroids(final int i, final List<Tweets> tweets) {

        Tweets newClusterCentroid = null;
        double minDistance = Integer.MAX_VALUE;
        for (int j = 0; j < tweets.size(); j++) {
            for (int k = j + 1; j < tweets.size(); j++) {
                double distance = getJaccardDistance(tweets.get(j), tweets.get(k));
                if (minDistance > distance) {
                    newClusterCentroid = tweets.get(k);
                    minDistance = distance;
                }
            }
        }
        centroidList.remove(i - 1);
        centroidList.add(i - 1, newClusterCentroid);

        return centroidList;

    }

    /*
     * Similarity measure for clustering : Jaccard simlarity
     */
    public double getJaccardDistance(final Tweets tweet1, final Tweets tweet2) {
        double jaccardDistance = 0.0;
        String[] tweetText1 = tweet1.getText().split("\\s");
        String[] tweetText2 = tweet2.getText().split("\\s");

        Set<String> ts1 = new HashSet<>();
        Set<String> ts2 = new HashSet<>();

        for (String wordInTweet1 : tweetText1) {
            ts1.add(wordInTweet1);
        }

        for (String wordInTweet2 : tweetText2) {
            ts2.add(wordInTweet2);
        }

        List<String> t1 = new ArrayList<>();
        t1.addAll(ts1);
        List<String> t2 = new ArrayList<>();
        t2.addAll(ts2);

        Collections.sort(t1);
        int intersect = 0;
        for (String s : t2) {
            if (Collections.binarySearch(t1, s) >= 0) {
                intersect++;
            }
        }
        int totalSize = t1.size() + t2.size();
        int union = totalSize - intersect;
        jaccardDistance = 1 - ((double) intersect / union);
        return jaccardDistance;
    }

    /*
     * Assign tweet to appropriate cluster.
     */
    public void assignClusterToTweet(final Long clusterId, final List<Double> list) {
        double min = list.get(0);
        int index = 0;
        for (int i = 1; i < list.size(); i++) {
            if (min > list.get(i)) {
                index = i;
                min = list.get(i);
            }
        }
        tweetMap.get(clusterId).setClusterId(index + 1);
    }
}
