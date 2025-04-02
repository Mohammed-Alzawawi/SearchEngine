package com.example.SearchEngine.utils.storage;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Snowflake {
    private static final Map<Long, SnowflakeNode> threadLastData = new ConcurrentHashMap<>();
    private static final Long defaultTimestamp = 1704432000000L;
    private static final Long maxStep = 12L;

    public Long generate(Long threadId){
        Long currentTimestamp = getCurrentTimestamp();
        if (!threadLastData.containsKey(threadId)) {
            threadLastData.put(threadId, new SnowflakeNode(0L, 0L));
        }
        if (currentTimestamp.equals(threadLastData.get(threadId).getTimestamp())){
            threadLastData.get(threadId).setStep(threadLastData.get(threadId).getStep() + 1);
            if (threadLastData.get(threadId).getStep() > ((1L << (maxStep + 1)) - 1)){
                threadLastData.get(threadId).setStep(0L);
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                currentTimestamp = getCurrentTimestamp();
            }
        } else {
            threadLastData.get(threadId).setStep(0L);
        }

        threadLastData.get(threadId).setTimestamp(currentTimestamp);
        return (currentTimestamp << 22) | (threadId << 12) | threadLastData.get(threadId).getStep();
    }

    private Long getCurrentTimestamp(){
        return Instant.now().toEpochMilli() - defaultTimestamp;
    }
}