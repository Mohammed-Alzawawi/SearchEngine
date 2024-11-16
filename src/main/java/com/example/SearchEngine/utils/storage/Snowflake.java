package com.example.SearchEngine.utils.storage;

import org.springframework.stereotype.Service;
import java.time.Instant;


@Service
public class Snowflake {
    private static Long step = 0L;
    private static final Long defultTimestamp = 1704432000000L;
    private static Long lastTimestamp = null;
    private static final Long maxStep = 5L;

    public synchronized String generate(Long threadId){
        Long currentTimestamp = getCurrentTimestamp();
        if (currentTimestamp.equals(lastTimestamp)){
            step++;
            if (step > (1L << maxStep)){
                step = 0L;
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lastTimestamp = currentTimestamp;
                currentTimestamp = getCurrentTimestamp();
            }
        } else {
            step = 0L;
        }

        lastTimestamp = currentTimestamp;
        return currentTimestamp.toString() + threadId + step.toString();
    }

    private Long getCurrentTimestamp(){
        return Instant.now().toEpochMilli() - defultTimestamp;
    }
}
