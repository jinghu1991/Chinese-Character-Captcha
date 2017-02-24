package com.hujing.captcha;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Created by jinghu1991 on 2017/2/22.
 */
public class BackgroundServlet extends HttpServlet {

    private static final int CHARACTER_SIZE = 24;
    private static final int WIDTH_BOUNDARY = 20;
    private static final int HEIGHT_BOUNDARY = 20;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 100;
    private static final int BG_IMAGE_COUNT = 25;
    private static final float ALPHA = 0.8f;
    // 集合中保存3500个常用汉字
    private String characterSet = "";
    // 系统可用字体名数组
    private String[] fontNameArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private java.util.List<BufferedImage> bgImageList = new ArrayList<>();

    public void init() throws ServletException {
        try {
            for (int i = 1; i <= BG_IMAGE_COUNT; ++i) {
                String path = "/bg-img/" + i + ".jpg";
                BufferedImage bufferedImage = ImageIO.read(BackgroundServlet.class.getResourceAsStream(path));
                bgImageList.add(bufferedImage);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BackgroundServlet.class.getResourceAsStream("/characters3500.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                characterSet += line;
            }
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("背景图片及汉字库初始化失败", e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Random random = new Random();
        // 随机的2~4个字作为验证码
        int targetCharCnt = 2 + random.nextInt(3);
        HashSet<Character> characterHashSet = new HashSet<>();
        StringBuilder wordBuffer = new StringBuilder();
        int index = 0;
        while (index++ < targetCharCnt) {
            char c = characterSet.charAt(random.nextInt(characterSet.length()));
            if (characterHashSet.contains(c)) { //不可有重复的字
                index--;
            } else {
                characterHashSet.add(c);
                wordBuffer.append(c);
            }
        }
        String characters = wordBuffer.toString();

        BufferedImage backgroundImage = generateBackground(characters);
        ImageIO.write(backgroundImage, "jpg", response.getOutputStream());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private BufferedImage generateBackground(String characters) throws IOException {
        Random random = new Random();

        int bgIndex = random.nextInt(BG_IMAGE_COUNT);
        BufferedImage bgImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bgImage.createGraphics();
        graphics2D.drawImage(bgImageList.get(bgIndex), 0, 0, null);

        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));

        java.util.List<Point> centerPointList = new ArrayList<>();
        for (int i = 0; i < characters.length(); ++i) {
            String fontName = fontNameArray[random.nextInt(fontNameArray.length)];
            // 随机字体
            graphics2D.setFont(new Font(fontName, Font.BOLD, CHARACTER_SIZE));
            // 随机颜色
            graphics2D.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            // 随机中心位置
            int centerX = 0;
            int centerY = 0;

            // 检测是否有交叠出现，最多迭代五次
            boolean isOverLapped = true;
            for (int iter = 0; isOverLapped && iter < 5; ++iter) {
                centerX = WIDTH_BOUNDARY + random.nextInt(IMAGE_WIDTH - 2 * WIDTH_BOUNDARY);
                centerY = HEIGHT_BOUNDARY + random.nextInt(IMAGE_HEIGHT - 2 * HEIGHT_BOUNDARY);
                isOverLapped = false;
                for (Point point : centerPointList) {
                    double distance = (point.getX() - centerX) * (point.getX() - centerX)
                            + (point.getY() - centerY) * (point.getY() - centerY);
                    if (distance < CHARACTER_SIZE * CHARACTER_SIZE) {
                        isOverLapped = true;
                    }
                }
            }

            centerPointList.add(new Point(centerX, centerY));

            // 旋转 -90 --- 90度
            int jiaodu = random.nextInt(180) - 90;
            double theta = jiaodu * Math.PI / 180;
            graphics2D.rotate(theta, centerX, centerY);

            System.out.println("centerX, centerY, theta: " + centerX + ", " + centerY + ", " + jiaodu);

            // 计算绘图位置，一定注意绘图位置为左下角
            int x = centerX - CHARACTER_SIZE / 2;
            int y = centerY + CHARACTER_SIZE / 2;

            // 绘制汉字到背景图
            graphics2D.drawString(String.valueOf(characters.charAt(i)), x, y);
            graphics2D.rotate(-theta, centerX, centerY);
        }

        // TODO 绘制干扰线

        graphics2D.dispose();
        return bgImage;
    }
}
