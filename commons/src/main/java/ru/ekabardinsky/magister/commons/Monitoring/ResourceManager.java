package ru.ekabardinsky.magister.commons.Monitoring;

/**
 * Created by ekabardinsky on 3/31/17.
 */
public class ResourceManager {
    private static byte[] lastResources;
    private static int lastSize;

    public static byte[] getTestResource(int size) {
        if (lastResources != null && size == lastSize) {
            return lastResources;
        }

        lastSize = size;
        lastResources = new byte[size];
        for (int i = 0; i < size; i++) {
            lastResources[i] = (byte)(i % 2);
        }
        return lastResources;
    }
}
