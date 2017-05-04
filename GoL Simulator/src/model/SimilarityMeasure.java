/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class SimilarityMeasure {

    private int generation, similairtyValue;

    public SimilarityMeasure(int mostSimilarGeneration, int similarityValue) {
        this.generation = mostSimilarGeneration;
        this.similairtyValue = similarityValue;
    }

    public int getGeneration() {
        return generation;
    }

    public int getSimilairtyValue() {
        return similairtyValue;
    }
}
