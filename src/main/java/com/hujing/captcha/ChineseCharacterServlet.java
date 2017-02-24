package com.hujing.captcha;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by jinghu1991 on 2017/2/22.
 */
public class ChineseCharacterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    // 集合中保存3500个常用汉字
    private String characters = "";
    public void init() throws ServletException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ChineseCharacterServlet.class.getResourceAsStream("/characters3500.txt")));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            characters = builder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int width = 120;
        int height = 30;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(getRandColor(200, 250));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);
        graphics.drawRect(0, 0, width - 1, height - 1);
        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.setFont(new Font("宋体", Font.BOLD, 18));

        Random random = new Random();
        StringBuilder wordBuffer = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            int index = random.nextInt(characters.length());
            wordBuffer.append(characters.charAt(index));
        }
        String word = wordBuffer.toString();

        int x = 10;
        for (int i = 0; i < word.length(); i++) {
            graphics2d.setColor(new Color(20 + random.nextInt(110), 20 + random
                    .nextInt(110), 20 + random.nextInt(110)));
            // 旋转 -30 --- 30度
            int jiaodu = random.nextInt(60) - 30;
            double theta = jiaodu * Math.PI / 180;

            char c = word.charAt(i);

            graphics2d.rotate(theta, x, 20);
            graphics2d.drawString(String.valueOf(c), x, 20);
            graphics2d.rotate(-theta, x, 20);
            x += 30;
        }

        System.out.println(word);

        // 绘制干扰线
        graphics.setColor(getRandColor(160, 200));
        int x1;
        int x2;
        int y1;
        int y2;
        for (int i = 0; i < 30; i++) {
            x1 = random.nextInt(width);
            x2 = random.nextInt(12);
            y1 = random.nextInt(height);
            y2 = random.nextInt(12);
            graphics.drawLine(x1, y1, x1 + x2, x2 + y2);
        }

        // 将上面图片输出到浏览器 ImageIO
        graphics.dispose();// 释放资源
        ImageIO.write(bufferedImage, "jpg", response.getOutputStream());

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * 取其某一范围的color
     *
     * @param fc
     *            int 范围参数1
     * @param bc
     *            int 范围参数2
     * @return Color
     */
    private Color getRandColor(int fc, int bc) {
        // 取其随机颜色
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
