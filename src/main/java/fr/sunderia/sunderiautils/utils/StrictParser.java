package fr.sunderia.sunderiautils.utils;

import java.util.Optional;

public class StrictParser {

    private StrictParser() {}

    public static Optional<Boolean> parseBoolean(String s) {
        if(s == null || !(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))) {
            return Optional.empty();
        }
        return Optional.of(Boolean.parseBoolean(s));
    }

    public static Optional<Integer> parseInteger(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(String s) {
        try {
            return Optional.of(Double.parseDouble(s));
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }
}