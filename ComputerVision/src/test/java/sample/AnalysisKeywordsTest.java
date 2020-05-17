package sample;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisKeywordsTest {

    @Test
    void testAnalyze() {
        File file = new File("C:\\Users\\godon\\__LMAOOO\\001-5032Z.png");
        HashSet<String> keywords = new HashSet<>();
        keywords.add("OFFICE USE ONLY");
        AnalysisKeywords.analyze(file, keywords);
    }
}