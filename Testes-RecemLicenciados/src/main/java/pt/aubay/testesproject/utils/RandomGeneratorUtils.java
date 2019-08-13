package pt.aubay.testesproject.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomGeneratorUtils {
	public static final Random gen = new Random();  
	public static int[] getRandomNumbers(int n, int maxRange) {  
	    assert n <= maxRange : "cannot get more unique numbers than the size of the range";  
	      
	    int[] result = new int[n];  
	    Set<Integer> used = new HashSet<Integer>();  
	      
	    for (int i = 0; i < n; i++) {  
	          
	        int newRandom;  
	        do {  
	            newRandom = gen.nextInt(maxRange);  
	        } while (used.contains(newRandom));  
	        result[i] = newRandom;  
	        used.add(newRandom);  
	    }
	    return result;  
	}  
}
