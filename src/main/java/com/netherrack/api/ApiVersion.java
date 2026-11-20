package com.netherrack.api;

/**
 * The API version a plugin declares in plugin.yml, and the version this jar implements.
 * <p>
 * Only the major and minor numbers matter while the API is in beta: a plugin built for 0.4.x
 * can run on any 0.4.x server, but not on 0.3.x or 1.x. The server checks this before loading a
 * plugin, so a mismatch is reported as a load failure rather than a class-loading error later.
 */
public final class ApiVersion {

    /** The version of the API in this jar, matching the version declared in plugin.yml. */
    public static final String CURRENT = "0.4.0";

    private ApiVersion() {
    }

    /** Whether a plugin declaring the given version can run against {@link #CURRENT}. */
    public static boolean isCompatible(String declared) {
        int[] plugin = majorMinor(declared);
        int[] current = majorMinor(CURRENT);
        return plugin != null && plugin[0] == current[0] && plugin[1] == current[1];
    }

    /** Reads the major and minor numbers, ignoring any patch, qualifier or trailing spaces. */
    private static int[] majorMinor(String version) {
        if (version == null) {
            return null;
        }
        String[] parts = version.strip().split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
