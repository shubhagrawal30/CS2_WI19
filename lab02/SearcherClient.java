package edu.caltech.cs2.lab02;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class SearcherClient {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean again = true;

        while (again) {
            // Ask for the method name
            String method = null;
            while (method == null || !List.of("linearSearch", "binarySearch").contains(method)) {
                if (method != null) {
                    System.out.println("That isn't a valid choice!");
                }
                System.out.print("Which method do you want to run (linearSearch, binarySearch)? ");
                method = in.nextLine();
            }

            // Ask for the number to search for
            Integer toSearch = null;
            while (toSearch == null) {
                System.out.print("Enter a number to search for: ");
                try {
                    toSearch = Integer.parseInt(in.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("That wasn't a valid number!");
                }
            }

            // Ask for the array to search in
            int[] arr = null;
            while (arr == null) {
                System.out.print("Enter a list of comma-separated numbers to search in: ");
                String[] strArr = in.nextLine().replaceAll("\\s+", "").split(",");
                int[] intArr = new int[strArr.length];
                try {
                    for (int i = 0; i < strArr.length; i++) {
                        intArr[i] = Integer.parseInt(strArr[i]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("That wasn't a valid number!");
                    continue;
                }
                arr = intArr;
            }

            if (method.equals("linearSearch")) {
                printMeaningOfAnswer(toSearch, arr, Searcher.linearSearch(toSearch, arr));
            } else {
                printMeaningOfAnswer(toSearch, arr, Searcher.binarySearch(toSearch, arr));
            }

            System.out.print("Go again (y/n)? ");
            String line = in.nextLine();
            again = line.toLowerCase().startsWith("y");
        }
    }

    public static void printMeaningOfAnswer(int toSearch, int[] arr, int result) {
        System.out.println("You searched for " + toSearch + " in the array " + Arrays.toString(arr) + ".");
        if (result >= 0) {
            System.out.println("It was found at index " + result + ".");
        }
        else {
            System.out.println("It was not found.  If it were to be put in the array, it would go at index " + (-result - 1) + ".");
        }
    }
}
