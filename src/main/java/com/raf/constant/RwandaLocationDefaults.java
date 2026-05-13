package com.raf.constant;

import java.util.List;

/**
 * Fallback province names aligned with {@code seeds.sql} / {@code generate_seeds.js} when the DB is empty.
 */
public final class RwandaLocationDefaults {

private RwandaLocationDefaults() {
}

/** Same ordering as {@code ORDER BY province} on seeded data (Eastern … Western). */
public static final List<String> PROVINCE_NAMES = List.of(
"Eastern Province",
"Kigali",
"Northern Province",
"Southern Province",
"Western Province"
);
}
