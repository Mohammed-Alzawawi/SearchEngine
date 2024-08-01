package com.example.SearchEngine.invertedIndex.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Bm25Calculator {
    private  final  Double K = 2.0 ;
    private  final  Double B = 0.75 ;


    public  Double CalculateScore(String schemaName , String fieldName , Integer documentId , Double tf , Double df) {
        Double documentLength = Double.valueOf(CollectionInfo.getDocumentLength(schemaName , documentId , fieldName ));
        Double numberOfDocument = Double.valueOf(CollectionInfo.getNumberOfDocument(schemaName));
        Double score = tf * (K+1) / tf + K *(1 - B + B *  (documentLength / numberOfDocument) ) ;
        Double IDF = Math.log((numberOfDocument - df + 0.5)  / (df + 0.5) +1 );
        return  score * IDF ;
    }



}
