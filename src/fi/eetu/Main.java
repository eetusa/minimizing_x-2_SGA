package fi.eetu;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        int BIT_COUNT = 16;                                 // needs to be higher (in binary) than max value. needs to be even number
        int MAX_VALUE = (int) Math.pow(2, BIT_COUNT) - 1;   // max value for possible values in population, zero is min
        int POPULATION_SIZE = 10;                           // population size. stays constant
        int MAX_ROUNDS = 1000000;
        int TRESHOLD_FOR_NO_NEW_BEST = 10000;

        double f_rc = 0.2;                                  // fraction of population to recombine
        double p_c = 0.8;                                   // chance of recombination
        double p_m = (double)1/(double)BIT_COUNT;           // chance for mutation per bit
        int n_rc = getEvenNumber(POPULATION_SIZE, f_rc);    // amount of population for recombination

        int counter_for_no_change_in_best = 0;
        int best = MAX_VALUE;

        System.out.println("Minimizing x^2 with a genetic algorithm.");
        System.out.println("Population: " + POPULATION_SIZE + ". Amount of individuals to recombine in pairs each generation: " + n_rc + " with chance of " +p_c+"." );
        System.out.println("Chance for individual bit mutation is " + p_m + ".");

        int population[];
        population = createPopulation(POPULATION_SIZE, MAX_VALUE);  // init random population


        for (int i = 0; i < MAX_ROUNDS; i++){
            int temp[] = getFittestOfPopulation(population, n_rc);  // choose a fraction of fittest population to possible recombine
            temp = recombinePairs(temp, p_c, BIT_COUNT);
            replaceParentsFromRecombinations(population, temp);
            mutatePopulation(population, p_m, BIT_COUNT);
            counter_for_no_change_in_best++;

            if (population[0] < best){
                counter_for_no_change_in_best = 0;
                best = population[0];
                System.out.println("New best: " + best + " on round: " + i);
            }
            if (counter_for_no_change_in_best == TRESHOLD_FOR_NO_NEW_BEST) break;
        }

        System.out.println("Smallest value in minimizing x^2 : x = " + best);
    }

    static void mutatePopulation(int[] population, double p_m, int bit_count){
        for (int i = 0; i < population.length; i++){
            population[i] = mutateInt(population[i], p_m, bit_count);
        }
    }

    static int mutateInt(int value, double p_m, int bit_count){
        StringBuilder byte_str =new StringBuilder(getByteStringFixedLength(value, bit_count));
        for (int i = 0; i < byte_str.length(); i++){
            Random r = new Random();
            boolean doesRecombine = r.nextDouble() <= p_m;

            if (doesRecombine){
                char c =  byte_str.charAt(i);
                if (c == '0'){
                    byte_str.setCharAt(i, '1');
                }else{
                    byte_str.setCharAt(i, '0');
                }
            }

        }
        return Integer.parseInt(byte_str.toString(),2);
    }

    static void replaceParentsFromRecombinations(int[] population, int[] children){
        int len = children.length;

        for (int i = 0; i < len; i++){
            population[i] = children[i];
        }
    }

    static int[] recombinePairs(int[] fittest, double p_c, int bit_count){
        for (int i = 0; i < fittest.length; i = i + 2){
            Random r = new Random();
            boolean doesRecombine = r.nextDouble() <= p_c;

            if (doesRecombine){
                int[] children = getTwoChildrenFromTwoParents(fittest[i], fittest[i+1], bit_count);
                fittest[i] = children[0];
                fittest[i+1] = children[1];
            }
        }

        return fittest;
    }

    static int[] getTwoChildrenFromTwoParents(int a, int b, int bit_count){
        int[] children;
        children = new int[2];
        int len = bit_count / 2;

        String parent_a = getByteStringFixedLength(a, bit_count);
        String parent_b = getByteStringFixedLength(b, bit_count);

        String child_a = parent_a.substring(0, len) + parent_b.substring(len, bit_count);
        String child_b = parent_b.substring(0,len) + parent_a.substring(len, bit_count);

        children[0] = Integer.parseInt(child_a, 2);
        children[1] = Integer.parseInt(child_b, 2);

//        System.out.println("Parent a: " + parent_a.substring(0,len) + " | " + parent_a.substring(len, bit_count));
//        System.out.println("Parent b: " + parent_b.substring(0,len) + " | " + parent_b.substring(len, bit_count));
//        System.out.println("Child a: " + child_a + " , " + children[0]);
//        System.out.println("Child b: " + child_b + " , " + children[1]);
//        System.out.println("-------------------");

        return children;
    }

    static int[] getFittestOfPopulation(int[] population, int n){
        int[] fittest_n;
        fittest_n = new int[n];
        sortPopulationByDescendingFitness(population);

        for (int i = 0; i < n; i++){
            fittest_n[i] = population[i];
        }

        return fittest_n;
    }

    static int getEvenNumber(int n, double d){
        int result = (int) Math.round(n * d);
        if (result % 2 != 0){
            result++;
        }

        return Math.min(result, n);
    }

    static void sortPopulationByDescendingFitness(int[] arr){
        Arrays.sort(arr);
    }

    static int[] createPopulation(int size, int max_value){
        int array[];
        array = new int[size];

        for (int i = 0; i < size; i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, max_value + 1);
            array[i] = randomNum;
        }

        return array;
    }
    static void printArray(int[] array){
        System.out.println("-------------");
        for (int a : array){
            System.out.println(a);
        }
        System.out.println("-------------");
    }

    static String getByteStringFixedLength(int i, int length){
        return addZeroes(Integer.toBinaryString(i), length);
    }

    static String addZeroes(String bit_string, int bit_count){
        int difference = bit_count - bit_string.length();
        if (difference == 0) return bit_string;

        while (difference > 0){
            bit_string = "0" + bit_string;
            difference--;
        }

        return bit_string;
    }
}
