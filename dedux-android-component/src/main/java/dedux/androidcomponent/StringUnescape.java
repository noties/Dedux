package dedux.androidcomponent;

import android.annotation.SuppressLint;

// all credits to the `http://stackoverflow.com/a/4298836/6745174`
// I just wonder how many classes there will be if _anyone_ decides to refactor...
class StringUnescape {

    @SuppressLint("DefaultLocale")
    static String unescape(String in) {

        final int length = in != null
                ? in.length()
                : 0;

        if (length == 0) {
            return in;
        }

        final StringBuilder out = new StringBuilder(length);

        boolean sawBackslash = false;

        int code;

        for (int i = 0; i < length; i++) {

            code = in.codePointAt(i);

            // UTF-16 handling
            if (code > Character.MAX_VALUE) {
                i++;
            }

            if (!sawBackslash) {
                if (code == '\\') {
                    sawBackslash = true;
                } else {
                    out.append(Character.toChars(code));
                }
                continue;
            }

            if (code == '\\') {
                sawBackslash = false;
                out.append('\\');
                out.append('\\');
                continue;
            }

            switch (code) {

                case 'r':
                    out.append('\r');
                    break;

                case 'n':
                    out.append('\n');
                    break;

                case 'f':
                    out.append('\f');
                    break;

                case 'b':
                    out.append("\\b");
                    break;

                case 't':
                    out.append('\t');
                    break;

                case 'a':
                    out.append('\007');
                    break;

                case 'e':
                    out.append('\033');
                    break;

                // A "control" character is what you get when you xor its
                // codepoint with '@'==64.  This only makes sense for ASCII,
                // and may not yield a "control" character after all.
                //
                // Strange but true: "\c{" is ";", "\c}" is "=", etc.
                case 'c': {
                    i += 1;
                    if (i == length) {
                        die("trailing \\c");
                    }
                    code = in.codePointAt(i);

                    // don't need to grok surrogates, as next line blows them up
                    if (code > 0x7f) {
                        die("expected ASCII after \\c");
                    }
                    out.append(Character.toChars(code ^ 64));
                    break;
                }

                case '8':
                case '9':
                    die("illegal octal digit");
                    // won't reach here


                    // may be 0 to 2 octal digits following this one
                    // so back up one for fallthrough to next case;
                    // unread this digit and fall through to next case.
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    i -= 1;
                    // FALLTHROUGH


                    // Can have 0, 1, or 2 octal digits following a 0
                    // this permits larger values than octal 377, up to
                    // octal 777.
                case '0': {

                    if (i + 1 == length) {
                        // found \0 at end of string
                        out.append(Character.toChars(0));
                        break;
                    }

                    i += 1;

                    int digits = 0;
                    int j;
                    for (j = 0; j <= 2; j++) {

                        if (i + j == length) {
                            break; // for loop
                        }

                        // safe because will unread surrogate
                        int ch = in.charAt(i + j);
                        if (ch < '0' || ch > '7') {
                            break; // for loop
                        }

                        digits += 1;
                    }

                    if (digits == 0) {
                        i -= 1;
                        out.append('\0');
                        break; // switch block
                    }

                    int value = 0;
                    try {
                        value = Integer.parseInt(in.substring(i, i + digits), 8);
                    } catch (NumberFormatException nfe) {
                        die("invalid octal value for \\0 escape");
                    }
                    out.append(Character.toChars(value));
                    i += digits - 1;
                    break; // switch block

                } // end case '0'

                case 'x': {

                    if (i + 2 > length) {
                        die("string too short for \\x escape");
                    }

                    i += 1;

                    boolean sawBrace = false;
                    if (in.charAt(i) == '{') {
                        // ok to ignore surrogates here
                        i += 1;
                        sawBrace = true;
                    }

                    int j;
                    for (j = 0; j < 8; j++) {

                        if (!sawBrace && j == 2) {
                            break; // for loop
                        }

                        // ASCII test also catches surrogates
                        int ch = in.charAt(i + j);
                        if (ch > 127) {
                            die("illegal non-ASCII hex digit in \\x escape");
                        }

                        if (sawBrace && ch == '}') {
                            break; // for loop
                        }

                        if (!((ch >= '0' && ch <= '9')
                                || (ch >= 'a' && ch <= 'f')
                                || (ch >= 'A' && ch <= 'F'))) {
                            die(String.format(
                                    "illegal hex digit #%d '%c' in \\x", ch, ch));
                        }

                    }

                    if (j == 0) {
                        die("empty braces in \\x{} escape");
                    }

                    int value = 0;
                    try {
                        value = Integer.parseInt(in.substring(i, i + j), 16);
                    } catch (NumberFormatException nfe) {
                        die("invalid hex value for \\x escape");
                    }

                    out.append(Character.toChars(value));
                    if (sawBrace) {
                        j += 1;
                    }

                    i += j - 1;
                    break; // switch statement
                }

                case 'u': {

                    if (i + 4 > length) {
                        die("string too short for \\u escape");
                    }

                    i += 1;

                    int j;
                    for (j = 0; j < 4; j++) {

                        // this also handles the surrogate issue
                        if (in.charAt(i + j) > 127) {
                            die("illegal non-ASCII hex digit in \\u escape");
                        }
                    }

                    int value = 0;
                    try {
                        value = Integer.parseInt(in.substring(i, i + j), 16);
                    } catch (NumberFormatException nfe) {
                        die("invalid hex value for \\u escape");
                    }
                    out.append(Character.toChars(value));
                    i += j - 1;
                    break; // switch statement
                }

                case 'U': {

                    if (i + 8 > length) {
                        die("string too short for \\U escape");
                    }

                    i += 1;

                    int j;
                    for (j = 0; j < 8; j++) {
                        // this also handles the surrogate issue
                        if (in.charAt(i + j) > 127) {
                            die("illegal non-ASCII hex digit in \\U escape");
                        }
                    }

                    int value = 0;
                    try {
                        value = Integer.parseInt(in.substring(i, i + j), 16);
                    } catch (NumberFormatException nfe) {
                        die("invalid hex value for \\U escape");
                    }
                    out.append(Character.toChars(value));
                    i += j - 1;
                    break; // switch statement
                }

                default:
                    out.append('\\');
                    out.append(Character.toChars(code));

                    break; /* switch */

            }

            sawBackslash = false;
        }

        // weird to leave one at the end
        if (sawBackslash) {
            out.append('\\');
        }

        return out.toString();
    }

    private static void die(String foa) {
        throw new IllegalArgumentException(foa);
    }

    private StringUnescape() {
    }
}
