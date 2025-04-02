package com.example.SearchEngine.utils.documentfilter;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class Merger {
    public List<Long> mergeTwoLists(List<Long> firstRange, List<Long> secondRange) {
        if (firstRange.isEmpty()) {
            return firstRange;
        }
        else if (secondRange.isEmpty()) {
            return secondRange;
        }
        List<Long> result = new ArrayList<>();
        int pointer1 = 0, pointer2 = 0;
        Long max = Math.max(firstRange.get(pointer1), secondRange.get(pointer2));
        pointer1 = lowerBound(firstRange, max);
        pointer2 = lowerBound(secondRange, max);
        while (pointer1 < firstRange.size() && pointer2 < secondRange.size()) {
            while ((pointer1 < firstRange.size()) && (pointer2 < secondRange.size()) && (Objects.equals(firstRange.get(pointer1), secondRange.get(pointer2)))) {
                result.add(firstRange.get(pointer1));
                pointer1++;
                pointer2++;
            }
            if (pointer1 >= firstRange.size() || pointer2 >= secondRange.size()) {
                break;
            }
            max = Math.max(firstRange.get(pointer1), secondRange.get(pointer2));
            pointer1 = lowerBound(firstRange, max);
            pointer2 = lowerBound(secondRange, max);
        }
        return result;
    }

    private int lowerBound(List<Long> targetList, long targetElement) {
        int result = Collections.binarySearch(targetList, targetElement);
        if (result < 0) {
            result = -result - 1;
        }
        return result;
    }
}
