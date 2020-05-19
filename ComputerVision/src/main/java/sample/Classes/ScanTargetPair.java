package sample;

public class ScanTargetPair {
    private ScanProperties keywordScan;
    private ScanProperties targetScan;

    public ScanTargetPair(ScanProperties keywordScan, ScanProperties targetScan) {
        this.keywordScan = keywordScan;
        this.targetScan = targetScan;
    }

    public ScanProperties getKeywordScan() {
        return keywordScan;
    }

    public void setKeywordScan(ScanProperties keywordScan) {
        this.keywordScan = keywordScan;
    }

    public ScanProperties getTargetScan() {
        return targetScan;
    }

    public void setTargetScan(ScanProperties targetScan) {
        this.targetScan = targetScan;
    }





}
