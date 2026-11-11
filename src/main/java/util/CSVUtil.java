
package com.cashmanagement.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CSVUtil {

    public static void initialize(
            String file, String header) throws IOException {

        Path path = Paths.get(file);

        Files.createDirectories(path.getParent());

        if (!Files.exists(path)) {
            Files.writeString(
                path,
                header + System.lineSeparator(),
                StandardCharsets.UTF_8
            );
        }
    }

    public static void append(
            String file, String[] values) throws IOException {

        StringJoiner joiner = new StringJoiner(",");

        for (String value : values) {
            joiner.add(escape(value));
        }

        Files.writeString(
            Paths.get(file),
            joiner + System.lineSeparator(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }

    public static List<String[]> read(String file)
            throws IOException {

        List<String[]> records = new ArrayList<>();

        Path path = Paths.get(file);

        if (!Files.exists(path)) {
            return records;
        }

        for (String line : Files.readAllLines(
                path, StandardCharsets.UTF_8)) {

            if (!line.isBlank()) {
                records.add(parse(line));
            }
        }

        return records;
    }

    public static void overwrite(
            String file, List<String[]> records)
            throws IOException {

        List<String> lines = new ArrayList<>();

        for (String[] record : records) {

            StringJoiner joiner = new StringJoiner(",");

            for (String value : record) {
                joiner.add(escape(value));
            }

            lines.add(joiner.toString());
        }

        Files.write(
            Paths.get(file),
            lines,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private static String escape(String value) {

        if (value == null) value = "";

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String[] parse(String line) {

        List<String> values = new ArrayList<>();

        StringBuilder value = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                if (insideQuotes &&
                    i + 1 < line.length() &&
                    line.charAt(i + 1) == '"') {

                    value.append('"');
                    i++;

                } else {
                    insideQuotes = !insideQuotes;
                }

            } else if (c == ',' && !insideQuotes) {

                values.add(value.toString());
                value.setLength(0);

            } else {
                value.append(c);
            }
        }

        values.add(value.toString());

        return values.toArray(new String[0]);
    }
}
