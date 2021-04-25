package helper;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public class Utils {

    private static final boolean[] tchar;
    private static final boolean[] fieldvchar;
    private static final Set<String> DISALLOWED_HEADERS_SET;
    public static final BiPredicate<String, String> ALLOWED_HEADERS;
    public static final BiPredicate<String, String> VALIDATE_USER_HEADER;

    static {
        TreeSet<String> treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(Set.of("connection", "content-length", "date", "expect", "from", "host", "upgrade", "via", "warning"));
        DISALLOWED_HEADERS_SET = Collections.unmodifiableSet(treeSet);

        ALLOWED_HEADERS = (header, unused) -> !DISALLOWED_HEADERS_SET.contains(header);
        VALIDATE_USER_HEADER = (name, value) -> {
            assert name != null : "null header name";

            // assert value != null EQUALS true
            // value != null EQUALS false => print out "null header value"
            assert value != null : "null header value";

            if (!isValidName(name)) {
                throw new IllegalArgumentException(String.format("invalid header name: \"%s\"", name));
            } else if (!ALLOWED_HEADERS.test(name, null)) {
                throw new IllegalArgumentException(String.format("restricted header name: \"%s\"", name));
            } else if (!isValidValue(value)) {
                throw new IllegalArgumentException(String.format("invalid header value for %s: \"%s\"", name, value));
            } else {
                return true;
            }
        };


        tchar = new boolean[256];
        fieldvchar = new boolean[256];
        char[] allowedTokenChars = "!#$%&'*+-.^_`|~0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int var2 = allowedTokenChars.length;
        char[] var7 = allowedTokenChars;
        for(int var3 = 0; var3 < var2; ++var3) {
            char c = var7[var3];
            tchar[c] = true;
        }

        for(char c = '!'; c < 255; ++c) {
            fieldvchar[c] = true;
        }

        fieldvchar[127] = false;
    }


    public static boolean isValidName(String token) {
        for(int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c > 255 || !tchar[c]) {
                return false;
            }
        }
        return !token.isEmpty();
    }

    public static boolean isValidValue(String token) {
        for(int i = 0; i < token.length(); ++i) {
            char c = token.charAt(i);
            if (c > 255) {
                return false;
            }

            if (c != ' ' && c != '\t' && !fieldvchar[c]) {
                return false;
            }
        }
        return true;
    }
}
