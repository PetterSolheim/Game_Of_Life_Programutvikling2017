package model;

/**
 * This is a wrapper class for sending a similarity measure between methods and objects.
 * It is used both in Statistics.java and StatisticsWindowController.java.
 */
public class SimilarityMeasure {

    private int generation, similairtyValue;

    /**
     * Constructor
     * @param mostSimilarGeneration the most similar generation of the measure.
     * @param similarityValue the similarity value
     */
    public SimilarityMeasure(int mostSimilarGeneration, int similarityValue) {
        this.generation = mostSimilarGeneration;
        this.similairtyValue = similarityValue;
    }

    /**
     *
     * @return <ocde>int</code> the most similar generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     *
     * @return <code>int</code> the similarity value.
     */
    public int getSimilairtyValue() {
        return similairtyValue;
    }
}
