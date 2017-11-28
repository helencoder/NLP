package com.helencoder.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 二维码图片识别(ZXing)
 *
 * Created by zhenghailun on 2017/11/28.
 */
public class QrcodeDistinguish {

    /**
     * 解析二维码图片
     *
     * @param imgPath (同时支持http及本地形式)
     * @return boolean true 是二维码图片
     */
    public static Boolean analysis(String imgPath) {
        Boolean flag = true;

        BufferedImage image;
        try {
            if (imgPath.contains("http")) {
                URL url = new URL(imgPath);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                image = ImageIO.read(connection.getInputStream());
            } else {
                image = ImageIO.read(new File(imgPath));
            }

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Result result = new MultiFormatReader().decode(binaryBitmap);// 对图像进行解码
            System.out.println(result);
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (NotFoundException e) {
            flag = false;
        }
        return flag;
    }

}
