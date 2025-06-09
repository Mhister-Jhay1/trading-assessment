package org.trading.system.common.util;

import java.util.UUID;

public class IdGenerationUtil {

    public static String generateId(){
        return UUID.randomUUID().toString()
                .replace("-","");
    }
}
