package com.zju.vis.print_backend.Utils;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
}

