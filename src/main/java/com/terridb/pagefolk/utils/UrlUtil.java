package com.terridb.pagefolk.utils;

import java.net.URI;
import java.util.Objects;

public class UrlUtil {

    public static URI generateUri(String path, Long id) {
        Objects.requireNonNull(id, "id must not be null");

        String normalizedPath = trimBoundarySlashes(path);
        if (normalizedPath.isEmpty()) {
            return URI.create("/" + id);
        }

        return URI.create("/" + normalizedPath + "/" + id);
    }

    private static String trimBoundarySlashes(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.replaceAll("^/+|/+$", "");
    }
}
