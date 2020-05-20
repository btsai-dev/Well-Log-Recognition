package louisiana.Classes;

public class ScanPair {
    private Scan keywordScan;
    private Scan targetScan;

    /**
     * Creates a ScanPair from a keywordScan and targetScan
     * @param keywordScan
     * @param targetScan
     */
    public ScanPair(Scan keywordScan, Scan targetScan) {
        this.keywordScan = keywordScan;
        this.targetScan = targetScan;
    }

    /**
     * Extracts keyword scan for the pair
     * @return
     */
    public Scan getKeywordScan() {
        return keywordScan;
    }

    /**
     * Sets the keyword scan for the pair
     * @param keywordScan
     */
    public void setKeywordScan(Scan keywordScan) {
        this.keywordScan = keywordScan;
    }

    /**
     * Extracts target scan for the pair
     * @return
     */
    public Scan getTargetScan() {
        return targetScan;
    }

    /**
     * Sets the target scan for the pair
     * @param targetScan
     */
    public void setTargetScan(Scan targetScan) {
        this.targetScan = targetScan;
    }





}
