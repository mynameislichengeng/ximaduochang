/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年8月3日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.util;

import java.util.Random;

/**
 * [功能说明]
 */
public class MathUtil {

    public static int getRandomNum(int min,int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
