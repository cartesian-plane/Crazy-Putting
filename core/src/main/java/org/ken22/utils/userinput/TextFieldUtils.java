package org.ken22.utils.userinput;

public class TextFieldUtils {
    public static double[] parseCoordinates(String text) {
        String[] parts = text.replace("(", "").replace(")", "").split(",");
        double[] coordinates = new double[2];
        coordinates[0] = Double.parseDouble(parts[0].trim());
        coordinates[1] = Double.parseDouble(parts[1].trim());
        return coordinates;
    }
}
