package dedux.androidcomponent;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EditModeHelper {

    private static final String STATE_PREFIX = "state_";

    static Map<String, String> buildAttributes(@Nonnull Context context, @Nullable AttributeSet attributeSet) {

        final Map<String, String> map;

        if (attributeSet == null) {
            //noinspection unchecked
            map = Collections.EMPTY_MAP;
        } else {

            map = new HashMap<>();

            final Resources resources = context.getResources();
            final int count = attributeSet.getAttributeCount();
            final int start = STATE_PREFIX.length();

            String name;
            String value;
            int resId;

            for (int i = 0; i < count; i++) {
                name = attributeSet.getAttributeName(i);
                if (name.startsWith(STATE_PREFIX)) {
                    name = name.substring(start);
                    resId = attributeSet.getAttributeResourceValue(i, 0);
                    if (resId != 0) {
                        value = getResourceAsString(resources, resId);
                        if (value != null) {
                            map.put(name, value);
                        }
                    } else {
                        map.put(name, attributeSet.getAttributeValue(i));
                    }
                }
            }
        }
        return map;
    }

    static String unescape(String in) {
        return StringUnescape.unescape(in);
    }

    @Nullable
    private static String getResourceAsString(Resources resources, int resId) {

        final String out;

        String typeName;
        try {
            typeName = resources.getResourceTypeName(resId);
        } catch (Resources.NotFoundException e) {
            typeName = null;
        }

        // we support very limited number of resources
        if (!TextUtils.isEmpty(typeName)) {

            final Object value;

            if ("bool".equals(typeName)) {
                value = resources.getBoolean(resId);
            } else if ("color".equals(typeName)) {
                value = resources.getColor(resId);
            } else if ("dimen".equals(typeName)) {
                value = resources.getDimensionPixelSize(resId);
            } else if ("integer".equals(typeName)) {
                value = resources.getInteger(resId);
            } else if ("string".equals(typeName)) {
                value = resources.getString(resId);
            } else {
                value = null;
            }

            if (value != null) {
                out = value.toString();
            } else {
                out = null;
            }
        } else {
            out = null;
        }

        return out;
    }
}
