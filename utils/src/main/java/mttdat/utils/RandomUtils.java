package mttdat.utils;

import java.util.List;
import java.util.Random;

/**
 * Created by swagsoft on 3/24/17.
 */

public class RandomUtils {

    static public int getRandomInt(int max, int min){

        Random r = new Random();

        return r.nextInt((max + 1) - min) + min;
    }

    static public float getRandomFloat(float max, float min){

        Random r = new Random();

        return min + r.nextFloat() * (max - min);
    }

    // Notice: cast to (Object[]) if passing an array.
    static public Object getRandomInArray(Object... list){

        Random r = new Random();

        int idx = r.nextInt((list.length));

        return list[idx];
    }

    static public Object getRandomInList(List list){

        Random r = new Random();

        int idx = r.nextInt((list.size()));

        return list.get(idx);
    }
}
