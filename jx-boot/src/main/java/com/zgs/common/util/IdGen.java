package com.zgs.common.util;

import java.util.UUID;

/**
 * UUID 生成
 * @author zgs
 */
public class IdGen {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
