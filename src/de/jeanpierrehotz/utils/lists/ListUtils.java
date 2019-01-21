package de.jeanpierrehotz.utils.lists;

import de.jeanpierrehotz.utils.numbers.RngUtils;

import java.util.*;

/**
 * This class provides some utility methods for handling lists
 */
public class ListUtils {

    /**
     * This method generates a list with random elements of the given pool while
     * every element is unique.
     * In case the size of the pool is too small the pool will simply be returned.
     *
     * @param pool The list of elements that are to be used.
     * @param n The amount of elements to take from the pool.
     * @param <T> The type of the elements of the list.
     * @return A list containing random but unique elements from the pool.
     */
    public static <T> List<T> generateListWithUniqueElements(List<T> pool, int n) {
        if(pool.size() < n) {
            System.out.println("The pool is too small for given size. (de.jeanpierrehotz.utils.lists.ListUtils.generateListWithUniqueElements(List, int):line 24)");
            return pool;
        }

        List<T> result = new ArrayList<>();
        Set<Integer> takenIndices = new HashSet<>();

        for(int i = 0; i < n; i++) {
            int ind;

            do {
                ind = RngUtils.random(0, pool.size() - 1);
            } while (takenIndices.contains(ind));

            takenIndices.add(ind);
            result.add(pool.get(ind));
        }

        return result;
    }

    /**
     * This method generates a list with random elements of the given pool while
     * every element is unique.
     * In case the size of the pool is too small the pool will simply be returned.
     *
     * @param pool The list of elements that are to be used.
     * @param n The amount of elements to take from the pool.
     * @param <T> The type of the elements of the list.
     * @return A list containing random but unique elements from the pool.
     */
    public static <T> List<T> generateListWithUniqueElements(T[] pool, int n) {
        return generateListWithUniqueElements(Arrays.asList(pool), n);
    }
}
