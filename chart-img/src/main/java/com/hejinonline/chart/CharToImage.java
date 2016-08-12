package com.hejinonline.chart;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hejinonline.chart.opencv.OpencvHandle;
import com.hejinonline.chart.util.FontUtil;
import com.hejinonline.chart.util.RandomUtil;
import com.hejinonline.chart.util.UnicodeCharUtil;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class CharToImage {

    /**
     * 待转字符
     */
    private String strChar;

    /**
     * 待转字符uft-8编码
     */
    private String uniName;

    /**
     * 图片宽度
     */
    private int width = 128;

    /**
     * 图片高度
     */
    private int height = 128;

    /**
     * 生成图片总数量
     */
    private int totalNum;

    /**
     * 已生成图片数量
     */
    private int nowNum;

    /**
     * 图片存放文件夹
     */
    private String folderPath;

    /**
     *
     */

    public CharToImage(String strChar, String strImgsFolder) {
        this.totalNum = 100;
        this.nowNum = 1;
        init(strChar, strImgsFolder);
    }

    public CharToImage(String strChar, String strImgsFolder, int totalNum) {
        this.totalNum = totalNum;
        this.nowNum = 1;
        init(strChar, strImgsFolder);
    }

    public CharToImage(String strChar, String strImgsFolder, int width, int height, int totalNum) {
        this.width = width;
        this.height = height;
        this.totalNum = totalNum;
        this.nowNum = 1;
        init(strChar, strImgsFolder);
    }

    /**
     * 初始化字符的存放路径
     *
     * @param strChar
     * @param strImgsFolder
     */
    private void init(String strChar, String strImgsFolder) {

        if (strChar != null && strChar.length() == 4) {
            this.uniName = strChar;
            this.strChar = UnicodeCharUtil.unicodeToChar("\\u" + strChar);
        } else {
            this.strChar = strChar;
            this.uniName = UnicodeCharUtil.charToUnicode(strChar);
        }

        this.uniName = "u_" + this.uniName;
        this.folderPath = strImgsFolder + "/" + this.uniName;
        try {
            File folder = new File(this.folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 生成字符图片样本
     *
     * @throws Exception
     */
    public void DrawImages() throws Exception {
        while (nowNum <= totalNum) {
            String imgName = folderPath + "/" + uniName + "_" + nowNum + ".jpg";
            //createImage(strChar, imgName);
            drawCharImage(strChar, imgName);
            nowNum++;
        }
    }

    /**
     * 生成字符图片操作
     *
     * @param str
     * @param imgName
     * @throws Exception
     */
    private void createImage(String str, String imgName) throws Exception {

        Font font = FontUtil.getRandomFont(str); //获取随机字体
        BufferedImage image = getBufferedImage(str, font); //生成字符图像
        Mat src = getMatByBufferedImage(image, image.getType(), CvType.CV_8UC3); //BufferedImage转Mat
        //暂时使用透视变换而非仿射变换
//        Mat mat = OpencvHandle.affineImage(src);//仿射变换
        Mat perspect_mat = OpencvHandle.perspectiveImage(src);//透视变换
        Mat rotate_mat = OpencvHandle.rotateImage(perspect_mat);//旋转图像
        Mat thresh_mat = OpencvHandle.thresholdImage(rotate_mat);//截取字符部分
        OpencvHandle.disturbImage(thresh_mat, RandomUtil.getRandomInt(0, 1));//加干扰线
        Mat blur_mat = OpencvHandle.blurredImage(thresh_mat);//去噪
        Mat resize_mat = OpencvHandle.resizeImage(blur_mat, width, height);//格式化大小

        //Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        //Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        //Imgproc.erode(mat, m1, erodeElement);// 腐蚀
        //Imgproc.dilate(mat, m2, dilateElement);// 膨胀

        Highgui.imwrite(imgName, resize_mat);
    }

    private void drawCharImage(String str, String imgName) {

        Font font = FontUtil.getRandomFont(str); //获取随机字体
        BufferedImage image = getBufferedImage(str, font); //生成字符图像
        Mat src = getMatByBufferedImage(image, image.getType(), CvType.CV_8UC3); //BufferedImage转Mat

        List<Integer> list = new ArrayList<>();

        list.add(0);
        list.add(1);
        list.add(4);
        list.add(6);
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(7);

        Mat handle_mat = src.clone();
        Mat dst = null;
        try {

            for (Integer i : list) {
                dst = randomOpencv(handle_mat, i.intValue());
                handle_mat = dst.clone();
            }
        } catch (Exception e) {

        }
        Highgui.imwrite(imgName, dst);
    }

    private Mat randomOpencv(Mat src, int handleOrder) {
        Mat dst = null;
        switch (handleOrder) {
            case 0:
                dst = OpencvHandle.perspectiveImage(src);//透视变换
                break;
            case 1:
                dst = OpencvHandle.rotateImage(src);//旋转图像
                break;
            case 2:
                dst = OpencvHandle.disturbImage(src, RandomUtil.getRandomInt(0, 1));//加干扰线
                break;
            case 3:
                dst = OpencvHandle.resizeImage(src, width, height);//格式化大小
                break;
            case 4:
                dst = OpencvHandle.thresholdImage(src);//截取字符部分
                break;
            case 5:
                dst = OpencvHandle.blurredImage(src);//去噪
                break;
            case 6:
                dst = OpencvHandle.dotArray(src);//点阵
                break;
            case 7:
                dst = OpencvHandle.addTexture(src);//纹理背景
                break;
            default:
                break;
        }

        return dst;
    }

    /**
     * 根据字符和字体获取BufferedImage
     *
     * @param str
     * @param font
     * @return
     */
    private BufferedImage getBufferedImage(String str, Font font) {

        Rectangle2D r = font.getStringBounds(str,
                new FontRenderContext(AffineTransform.getScaleInstance(1, 1), false, false));

        // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
        int width = (int) Math.round(r.getWidth()) + 1;
        int height = (int) Math.round(r.getHeight()) + 1;

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.BLACK);// 在换成白色
        g.setFont(font);// 设置画笔字体
        g.drawString(str, 0, (int) (font.getSize() * 0.9));// 画出字符串
        g.dispose();

        return image;
    }

    /**
     * 将BufferedImage转换为Mat
     *
     * @param image
     * @param imgType
     * @param matType
     * @return
     */
    public Mat getMatByBufferedImage(BufferedImage image, int imgType, int matType) {
        if (image == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (image.getType() != imgType) {

            // Create a buffered image
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = newImage.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(image, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(image.getHeight(), image.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }

    public String getStrChar() {
        return strChar;
    }

    public void setStrChar(String strChar) {
        this.strChar = strChar;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getNowNum() {
        return nowNum;
    }

    public void setNowNum(int nowNum) {
        this.nowNum = nowNum;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Char2Img [strChar=" + strChar + ", uniName=" + uniName + ", totalNum=" + totalNum + ", nowNum=" + nowNum
                + ", folderPath=" + folderPath + "]";
    }

}
