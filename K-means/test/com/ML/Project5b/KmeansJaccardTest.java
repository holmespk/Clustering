package com.ML.Project5b;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KmeansJaccardTest {

    @Test
    public void jaccardDistanceTest() {

        double distance = 0.0;
        Tweets tweet1 = new Tweets();
        String s1 = "the long march";
        tweet1.setText(s1);

        Tweets tweet2 = new Tweets();
        String s2 = "ides of march";
        tweet2.setText(s2);

        KMeansJaccard kmj = new KMeansJaccard(25);
        distance = kmj.getJaccardDistance(tweet1, tweet2);

        assertEquals("Unexpected Jacccard Distance", distance, 0.8, 0.0);
    }

}
