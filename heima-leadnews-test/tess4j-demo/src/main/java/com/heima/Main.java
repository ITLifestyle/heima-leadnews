package com.heima;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Main {
    public static void main(String[] args) throws TesseractException {
        // 创建实例
        ITesseract tesseract = new Tesseract();
        // 设置字体路径
        tesseract.setDatapath("D:\\IT\\my_project\\_learn\\java\\real_object\\heima-leadnews\\_resource\\tessdata");
        // 设置语言 --> 简体中文
        tesseract.setLanguage("chi_sim");

        File file = new File("D:\\media\\images\\640 (1).jpg");
        // 识别图片
        String result = tesseract.doOCR(file);
        // 去掉换行符, 加 - 是为了避免去掉换行符后, 两端前后拼接组成敏感词
        System.out.println(result.replaceAll("\\r|\\n", "-"));
    }
}