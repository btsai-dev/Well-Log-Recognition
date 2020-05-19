package sample.Classes;

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

    public Scan getKeywordScan() {
        return keywordScan;
    }

    public void setKeywordScan(Scan keywordScan) {
        this.keywordScan = keywordScan;
    }

    public Scan getTargetScan() {
        return targetScan;
    }

    public void setTargetScan(Scan targetScan) {
        this.targetScan = targetScan;
    }





}
