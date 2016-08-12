package com.hejinonline.chart;

import org.opencv.core.Core;


/**
 * Created by zhangyayun on 16-6-20.
 */
public class CharToImageTest {
    @org.junit.Test
    public void createImage() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        String folderPath =  System.getProperty("user.dir") + "/images";
//        for(int num = 0x4E00;num<0x9FA5;num++) {
//            CharToImage cti = new CharToImage(Integer.toHexString(num),folderPath,100);
//            cti.DrawImages();
//        }

        String folderPath =  System.getProperty("user.dir") + "/img2";
        for(int num = 0x6000;num<=0x600F;num++){
            CharToImage cti = new CharToImage(Integer.toHexString(num),folderPath,300);
            cti.DrawImages();
        }

    }

}