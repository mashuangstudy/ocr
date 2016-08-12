package com.hejinonline.chart.opencv;

import com.hejinonline.chart.util.RandomUtil;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;


public class OpencvHandle {

    /**
     * 仿射变换(变换为平行四边形)
     *
     * @param src
     * @return
     */
    public static Mat affineImage(Mat src) {

        MatOfPoint2f srcTri;
        MatOfPoint2f dstTri;

        srcTri = new MatOfPoint2f(new Point(0, 0), new Point(src.cols() - 1, 0), new Point(0, src.rows() - 1));
        dstTri = new MatOfPoint2f(new Point(src.cols() * RandomUtil.getRandomDouble(0.0, 0.1), src.rows() * RandomUtil.getRandomDouble(0.0, 0.1)),
                new Point(src.cols() * RandomUtil.getRandomDouble(0.9, 1.0), src.rows() * RandomUtil.getRandomDouble(0.0, 0.1)),
                new Point(src.cols() * RandomUtil.getRandomDouble(0.0, 0.1), src.rows() * RandomUtil.getRandomDouble(0.9, 1.0)));

        Mat warp_mat = new Mat(2, 3, CvType.CV_32FC1);
        Mat dst = Mat.zeros(src.rows(), src.cols(), src.type());

        warp_mat = Imgproc.getAffineTransform(srcTri, dstTri);
        Imgproc.warpPerspective(src, dst, warp_mat, dst.size(), Imgproc.INTER_LINEAR | Imgproc.CV_WARP_FILL_OUTLIERS, 0, Scalar.all(255));

        return dst;
    }

    /**
     * 透视变换(可以变换为梯形等四边形)
     *
     * @param src
     * @return
     */
    public static Mat perspectiveImage(Mat src) {

        MatOfPoint2f srcTri;
        MatOfPoint2f dstTri;
        srcTri = new MatOfPoint2f(new Point(0, 0), new Point(src.cols() - 1, 0), new Point(0, src.rows() - 1), new Point(src.cols() - 1, src.rows() - 1));
        dstTri = new MatOfPoint2f(new Point(src.cols() * RandomUtil.getRandomDouble(0.0, 0.2), src.rows() * RandomUtil.getRandomDouble(0.0, 0.2)),
                new Point(src.cols() * RandomUtil.getRandomDouble(0.8, 1.0), src.rows() * RandomUtil.getRandomDouble(0.0, 0.2)),
                new Point(src.cols() * RandomUtil.getRandomDouble(0.0, 0.1), src.rows() * RandomUtil.getRandomDouble(0.9, 1.0)),
                new Point(src.cols() * RandomUtil.getRandomDouble(0.9, 1.0), src.rows() * RandomUtil.getRandomDouble(0.9, 1.0)));

        Mat warp_mat = new Mat(2, 3, CvType.CV_32FC1);
        Mat dst = Mat.zeros(src.rows(), src.cols(), src.type());

        warp_mat = Imgproc.getPerspectiveTransform(srcTri, dstTri);
        Imgproc.warpPerspective(src, dst, warp_mat, dst.size(), Imgproc.INTER_LINEAR | Imgproc.CV_WARP_FILL_OUTLIERS, 0, Scalar.all(255));

        return dst;
    }

    /**
     * 旋转图像(围绕中心旋转)
     *
     * @param src
     * @return
     */
    public static Mat rotateImage(Mat src) {

        /// 旋转矩阵
        int width = src.cols();
        int height = src.rows();
        double degree = RandomUtil.getRandomDouble(-5, 5);
//        double scale = 1;
        double scale = RandomUtil.getRandomDouble(0.9, 1.1);
        double angle = Math.PI * degree / 180;
        double a = Math.sin(angle);
        double b = Math.cos(angle);
        int width_rotate = (int) (width * Math.abs(b) + height * Math.abs(a));
        int height_rotate = (int) (width * Math.abs(a) + height * Math.abs(b));
        Point center = new Point(width / 2, height / 2);

        Mat warp_mat = new Mat(2, 3, 8);
        Mat dst = Mat.zeros(width_rotate, height_rotate, src.type());

        warp_mat = Imgproc.getRotationMatrix2D(center, degree, scale);
        warp_mat.put(0, 2, warp_mat.get(0, 2)[0] + (width_rotate - width) / 2);
        warp_mat.put(1, 2, warp_mat.get(1, 2)[0] + (height_rotate - height) / 2);
        Imgproc.warpAffine(src, dst, warp_mat, dst.size(), Imgproc.INTER_LINEAR | Imgproc.CV_WARP_FILL_OUTLIERS, 0, Scalar.all(255));

        return dst;
    }

    /**
     * 格式化大小
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static Mat resizeImage(Mat src, int width, int height) {
        Size size = new Size(width, height);
        Mat dst = Mat.zeros(size, src.type());
        Imgproc.resize(src, dst, size);
        return dst;
    }

    /**
     * 截取字符
     *
     * @param src
     * @return
     */
    public static Mat thresholdImage(Mat src) {
        Rect rect = getImageMinRect(src);
        Mat dst = new Mat(src, rect);
        return dst;
    }

    /**
     * 添加纹理背景
     *
     * @param src
     * @return
     */
    public static Mat addTexture(Mat src) {
        Mat bg = getRandomTextureBg();
        Mat dst = new Mat(bg, new Rect(RandomUtil.getRandomInt(0, bg.width() - src.width()), RandomUtil.getRandomInt(0, bg.height() - src.height()), src.width(), src.height()));
        Core.addWeighted(dst, RandomUtil.getRandomDouble(0.4, 0.6), src, RandomUtil.getRandomDouble(0.4, 0.6), 0, dst, dst.depth());
        return dst;
    }

    /**
     * 点阵效果
     *
     * @param src
     * @return
     */
    public static Mat dotArray(Mat src) {
        if (RandomUtil.getRandomInt(0, 1) == 1) {
            for (int i = 0; i < src.width(); i++) {
                for (int j = 0; j < src.height(); j++) {
                    if (i % 3 == 2 || j % 3 == 2) {
                        src.put(j, i, 255);
                    }
                }
            }
        }
        return src;
    }

//    public static Mat add

    /**
     * 图像去噪
     *
     * @param src
     * @return
     */
    public static Mat blurredImage(Mat src) {

        Mat dst = Mat.zeros(src.rows(), src.cols(), src.type());

        int blur_size = RandomUtil.getRandomInt(2, 6) * 2 - 1;
//        Imgproc.blur(mat, dst, new Size(blur_size, blur_size));// 均值滤波
//        Imgproc.boxFilter(src,dst,-1,new Size(blur_size,blur_size));// 方框滤波
        //除双边滤波外区别不大,暂时使用高斯滤波
        Imgproc.GaussianBlur(src,dst, new Size(blur_size,blur_size),0,0);//高斯滤波
//        Imgproc.medianBlur(src,dst,blur_size);//中值滤波
//        Imgproc.bilateralFilter(src,dst,25,25*2,25/2);//双边滤波
        return dst;
    }

    /**
     * 画干扰线
     *
     * @param src
     * @param lineNum
     */
    public static Mat disturbImage(Mat src, int lineNum) {
        Point pt1 = new Point();
        Point pt2 = new Point();

        for (int i = 0; i < lineNum; i++) {
            pt1.x = RandomUtil.getRandomDouble(src.cols());
            pt1.y = RandomUtil.getRandomDouble(src.rows());
            pt2.x = RandomUtil.getRandomDouble(src.cols());
            pt2.y = RandomUtil.getRandomDouble(src.rows());
            Core.line(src, pt1, pt2, RandomUtil.getRandomInt(0, 1) == 1 ? new Scalar(0, 0, 0) : new Scalar(255, 255, 255), RandomUtil.getRandomInt(2, 4));
        }
        return src;
    }


    /**
     * 获取图像白色部分的最小矩形
     *
     * @param src
     * @return
     */
    private static Rect getImageMinRect(Mat src) {

        int height_src = src.height();
        int width_src = src.width();
        int minHeight = height_src, maxHeight = 0, minWidth = width_src, maxWidth = 0;
        boolean flag1 = true, flag2 = true;

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);


        for (int i = 0; i < width_src && flag1; i++) {
            for (int j = 0; j < height_src; j++) {
                if (minWidth < width_src && maxWidth > 0) {
                    flag1 = false;
                    break;
                }
                if (src.get(j, width_src - 1 - i)[0] == 0) {
                    maxWidth = (width_src - 1 - i) > maxWidth ? (width_src - 1 - i) : maxWidth;
                }
                if (src.get(j, i)[0] == 0) {
                    minWidth = i < minWidth ? i : minWidth;
                }
            }
        }

        for (int j = 0; j < height_src && flag2; j++) {
            for (int i = 0; i < width_src; i++) {
                if (minHeight < height_src && maxHeight > 0) {
                    flag2 = false;
                    break;
                }
                if (src.get(j, i)[0] == 0) {
                    minHeight = j < minHeight ? j : minHeight;
                }
                if (src.get(height_src - 1 - j, i)[0] == 0) {
                    maxHeight = (height_src - 1 - j) > maxHeight ? (height_src - 1 - j) : maxHeight;
                }
            }
        }

        int width = maxWidth - minWidth;
        int height = maxHeight - minHeight;
        if (width < 50) {
            width += 50;
            minWidth = minWidth - 25 >= 0 ? minWidth - 25 : 0;
        }
        if (height < 50) {
            height += 50;
            minHeight = minHeight - 25 >= 0 ? minHeight - 25 : 0;
        }
        return new Rect(minWidth, minHeight, width, height);
    }

    /**
     * 获取随机纹理背景
     *
     * @return
     */
    private static Mat getRandomTextureBg() {
        File file = new File(System.getProperty("user.dir") + "/bgimgs");
        String imgPath = file.listFiles()[RandomUtil.getRandomInt(0, file.listFiles().length - 1)].getPath();
        Mat mat_bg = Highgui.imread(imgPath, 0);
        return mat_bg;
    }

}
