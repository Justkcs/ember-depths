package com.emberdepths.gui;

import com.emberdepths.domain.hero.HeroClass;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.world.RoomType;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public final class SpriteRenderer {

    private static final Color OUTLINE = new Color(0x15161C);
    private static final Color SKIN = new Color(0xE9C29B);
    private static final Color BROWN = new Color(0x7A4E2A);
    private static final Color DARK_BROWN = new Color(0x4A2F1A);
    private static final Color GOLD = new Color(0xE8B93C);
    private static final Color STEEL_LIGHT = new Color(0xC9D0DC);
    private static final Color STEEL_DARK = new Color(0x5C6475);
    private static final Color BLADE_LIGHT = new Color(0xF4F6FA);
    private static final Color BLADE_DARK = new Color(0xA2AABA);
    private static final Color BONE = new Color(0xE8E2CC);

    private SpriteRenderer() {
    }

    public static void drawHero(Graphics2D g, HeroClass heroClass, double x, double y, double size, float alpha) {
        Graphics2D s = begin(g, x, y, size, alpha, false);
        shadow(s);
        switch (heroClass) {
            case WARRIOR -> warrior(s);
            case MAGE -> mage(s);
            case ROGUE -> rogue(s);
        }
        s.dispose();
    }

    public static void drawMonster(Graphics2D g, Monster monster, double x, double y, double size, float alpha) {
        Graphics2D s = begin(g, x, y, size, alpha, true);
        shadow(s);
        switch (monster.getType()) {
            case GOBLIN -> goblin(s);
            case SKELETON -> skeleton(s);
            case ORC -> orc(s);
            case DRAGON -> dragon(s, monster.isCharging());
        }
        s.dispose();
    }

    public static void drawRoomFeature(Graphics2D g, RoomType type, double x, double y, double size, long time) {
        Graphics2D s = begin(g, x, y, size, 1f, false);
        switch (type) {
            case TREASURE -> chest(s, time);
            case SPRING -> spring(s, time);
            default -> door(s);
        }
        s.dispose();
    }

    private static Graphics2D begin(Graphics2D g, double x, double y, double size, float alpha, boolean mirrored) {
        Graphics2D s = (Graphics2D) g.create();
        s.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        s.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
        s.translate(x, y);
        s.scale(size / 100.0, size / 100.0);
        if (mirrored) {
            s.translate(100, 0);
            s.scale(-1, 1);
        }
        s.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return s;
    }

    private static void shadow(Graphics2D s) {
        s.setColor(new Color(0, 0, 0, 100));
        s.fill(oval(16, 88, 68, 10));
    }

    private static void part(Graphics2D s, Shape shape, Paint paint) {
        s.setPaint(paint);
        s.fill(shape);
        s.setColor(OUTLINE);
        s.draw(shape);
    }

    private static void fill(Graphics2D s, Shape shape, Paint paint) {
        s.setPaint(paint);
        s.fill(shape);
    }

    private static void line(Graphics2D s, Color color, float width, double x1, double y1, double x2, double y2) {
        s.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        s.setColor(color);
        s.draw(new Line2D.Double(x1, y1, x2, y2));
        s.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    private static Paint vertical(double top, Color topColor, double bottom, Color bottomColor) {
        return new GradientPaint(0f, (float) top, topColor, 0f, (float) bottom, bottomColor);
    }

    private static Paint glow(double cx, double cy, double radius, Color inner, Color outer) {
        return new RadialGradientPaint(new Point2D.Double(cx, cy), (float) radius,
                new float[]{0f, 1f}, new Color[]{inner, outer});
    }

    private static Shape poly(double... points) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            path.lineTo(points[i], points[i + 1]);
        }
        path.closePath();
        return path;
    }

    private static Shape oval(double x, double y, double w, double h) {
        return new Ellipse2D.Double(x, y, w, h);
    }

    private static Shape rect(double x, double y, double w, double h, double radius) {
        return new RoundRectangle2D.Double(x, y, w, h, radius, radius);
    }

    private static void warrior(Graphics2D s) {
        Color legs = new Color(0x3B3F4A);
        part(s, rect(37, 68, 11, 20, 3), legs);
        part(s, rect(52, 68, 11, 20, 3), legs);
        part(s, rect(34, 84, 16, 8, 3), BROWN);
        part(s, rect(50, 84, 16, 8, 3), BROWN);
        part(s, rect(31, 38, 38, 36, 10), vertical(38, STEEL_LIGHT, 74, STEEL_DARK));
        part(s, rect(31, 60, 38, 6, 2), BROWN);
        fill(s, rect(47, 60, 6, 6, 1), GOLD);
        part(s, poly(71, 54, 77, 54, 92, 10, 88, 7), vertical(7, BLADE_LIGHT, 54, BLADE_DARK));
        part(s, rect(62, 40, 10, 22, 5), vertical(40, STEEL_LIGHT, 62, STEEL_DARK));
        part(s, rect(63, 52, 22, 5, 2), GOLD);
        part(s, rect(71, 57, 5, 10, 2), BROWN);
        part(s, oval(14, 40, 28, 36), vertical(40, new Color(0xC2453D), 76, new Color(0x7E231F)));
        s.setStroke(new BasicStroke(2.4f));
        s.setColor(GOLD);
        s.draw(oval(17.5, 43.5, 21, 29));
        s.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        part(s, oval(25, 54, 6, 8), GOLD);
        part(s, poly(46, 10, 50, 0, 61, 1, 68, 10, 57, 7), new Color(0xD2402F));
        part(s, oval(35, 8, 30, 34), vertical(8, STEEL_LIGHT, 42, STEEL_DARK));
        fill(s, rect(43, 22, 20, 5, 2), new Color(0x1A1C22));
        line(s, new Color(0xFFFFFF, false), 1.2f, 40, 14, 44, 11);
    }

    private static void mage(Graphics2D s) {
        Color robeLight = new Color(0x7B5CD6);
        Color robeDark = new Color(0x3A2878);
        line(s, BROWN, 4f, 76, 92, 80, 22);
        fill(s, oval(62, 0, 36, 36), glow(80, 18, 18, new Color(92, 200, 255, 140), new Color(92, 200, 255, 0)));
        part(s, oval(72, 10, 16, 16), glow(78, 15, 10, new Color(0xE6FBFF), new Color(0x2E9BD6)));
        part(s, poly(36, 38, 64, 38, 76, 92, 24, 92), vertical(38, robeLight, 92, robeDark));
        fill(s, rect(24.5, 86, 51, 5, 1), GOLD);
        fill(s, rect(38, 58, 24, 4, 1), GOLD);
        part(s, poly(60, 42, 70, 46, 80, 58, 74, 63, 62, 52), robeLight);
        part(s, oval(73, 55, 9, 9), SKIN);
        part(s, oval(39, 22, 22, 22), SKIN);
        fill(s, oval(45, 30, 3, 3), OUTLINE);
        fill(s, oval(53, 30, 3, 3), OUTLINE);
        part(s, poly(40, 37, 60, 37, 57, 50, 50, 60, 43, 50), new Color(0xE4E4EE));
        part(s, oval(26, 22, 48, 9), robeDark);
        part(s, poly(34, 26, 66, 26, 60, 10, 72, -2, 52, 5), vertical(-2, robeLight, 26, robeDark));
        fill(s, poly(50, 12, 52, 16, 56, 16, 53, 19, 54, 23, 50, 20, 46, 23, 47, 19, 44, 16, 48, 16), GOLD);
    }

    private static void rogue(Graphics2D s) {
        Color cloakLight = new Color(0x3E7A68);
        Color cloakDark = new Color(0x1F3F36);
        part(s, poly(30, 36, 70, 36, 77, 90, 23, 90), vertical(36, cloakLight, 90, cloakDark));
        part(s, rect(39, 72, 9, 16, 3), new Color(0x2A2A30));
        part(s, rect(52, 72, 9, 16, 3), new Color(0x2A2A30));
        part(s, rect(36, 84, 14, 8, 3), DARK_BROWN);
        part(s, rect(50, 84, 14, 8, 3), DARK_BROWN);
        part(s, poly(40, 40, 60, 40, 62, 74, 38, 74), new Color(0x4A4A55));
        part(s, rect(38, 62, 24, 4, 1), BROWN);
        fill(s, rect(48, 62, 4, 4, 1), GOLD);
        part(s, poly(68, 60, 72, 57, 90, 42, 87, 40), vertical(40, BLADE_LIGHT, 60, BLADE_DARK));
        part(s, poly(32, 60, 28, 57, 10, 42, 13, 40), vertical(40, BLADE_LIGHT, 60, BLADE_DARK));
        part(s, oval(64, 56, 9, 9), DARK_BROWN);
        part(s, oval(27, 56, 9, 9), DARK_BROWN);
        part(s, poly(50, 2, 60, 14, 40, 14), cloakLight);
        part(s, oval(33, 10, 34, 34), vertical(10, cloakLight, 44, cloakDark));
        fill(s, oval(40, 20, 20, 18), new Color(0x101418));
        fill(s, oval(43, 26, 5, 3), new Color(0xF5D547));
        fill(s, oval(52, 26, 5, 3), new Color(0xF5D547));
        part(s, rect(40, 33, 20, 8, 4), new Color(0x33363F));
    }

    private static void goblin(Graphics2D s) {
        Color light = new Color(0x7BBF4A);
        Color dark = new Color(0x4C8A2E);
        part(s, rect(40, 76, 7, 12, 3), dark);
        part(s, rect(53, 76, 7, 12, 3), dark);
        part(s, oval(36, 84, 12, 6), dark);
        part(s, oval(52, 84, 12, 6), dark);
        part(s, oval(34, 48, 32, 30), vertical(48, light, 78, dark));
        part(s, poly(38, 68, 62, 68, 58, 82, 42, 82), BROWN);
        part(s, rect(26, 52, 9, 20, 4), light);
        part(s, poly(66, 66, 71, 66, 74, 44, 69, 42), vertical(42, BLADE_LIGHT, 66, BLADE_DARK));
        part(s, rect(64, 52, 9, 20, 4), light);
        part(s, poly(33, 36, 8, 22, 34, 28), light);
        part(s, poly(67, 36, 92, 22, 66, 28), light);
        part(s, oval(30, 18, 40, 34), vertical(18, light, 52, dark));
        part(s, oval(38, 28, 10, 9), new Color(0xF8E36A));
        part(s, oval(52, 28, 10, 9), new Color(0xF8E36A));
        fill(s, oval(42, 31, 4, 4), new Color(0xB81D1D));
        fill(s, oval(56, 31, 4, 4), new Color(0xB81D1D));
        fill(s, oval(47, 36, 6, 5), dark);
        fill(s, rect(41, 43, 18, 5, 3), new Color(0x2A1414));
        fill(s, poly(43, 43, 45, 47, 47, 43), Color.WHITE);
        fill(s, poly(53, 43, 55, 47, 57, 43), Color.WHITE);
    }

    private static void skeleton(Graphics2D s) {
        line(s, BONE, 3.5f, 50, 34, 50, 66);
        line(s, BONE, 3f, 42, 40, 58, 40);
        line(s, BONE, 3f, 41, 46, 59, 46);
        line(s, BONE, 3f, 42, 52, 58, 52);
        line(s, BONE, 3.5f, 45, 70, 43, 88);
        line(s, BONE, 3.5f, 55, 70, 57, 88);
        part(s, oval(37, 85, 10, 5), BONE);
        part(s, oval(53, 85, 10, 5), BONE);
        part(s, oval(40, 62, 20, 10), BONE);
        line(s, BONE, 3.5f, 42, 38, 36, 56);
        line(s, BONE, 3.5f, 36, 56, 40, 66);
        line(s, BONE, 3.5f, 58, 38, 70, 50);
        line(s, BONE, 3.5f, 70, 50, 78, 50);
        s.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        s.setColor(BROWN);
        s.draw(new Arc2D.Double(66, 22, 24, 56, 90, -180, Arc2D.OPEN));
        s.setStroke(new BasicStroke(1f));
        s.setColor(new Color(0xD8D0B8));
        s.draw(new Line2D.Double(78, 22, 78, 78));
        s.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        part(s, oval(38, 8, 24, 24), vertical(8, Color.WHITE, 32, BONE));
        part(s, rect(42, 27, 16, 8, 3), BONE);
        fill(s, oval(42, 15, 6, 7), new Color(0x1A1414));
        fill(s, oval(52, 15, 6, 7), new Color(0x1A1414));
        fill(s, oval(44, 17, 2.5, 3), new Color(0xFF4A3A));
        fill(s, oval(54, 17, 2.5, 3), new Color(0xFF4A3A));
        fill(s, poly(49, 23, 51, 23, 50, 26), new Color(0x1A1414));
        line(s, new Color(0x1A1414), 1f, 46, 30, 46, 34);
        line(s, new Color(0x1A1414), 1f, 50, 30, 50, 34);
        line(s, new Color(0x1A1414), 1f, 54, 30, 54, 34);
    }

    private static void orc(Graphics2D s) {
        Color light = new Color(0x6E9E4A);
        Color dark = new Color(0x3F6A2C);
        line(s, BROWN, 5f, 78, 92, 82, 16);
        part(s, poly(80, 12, 99, 4, 100, 32, 82, 30), vertical(4, STEEL_LIGHT, 32, STEEL_DARK));
        part(s, rect(36, 72, 12, 16, 3), new Color(0x4A3A2A));
        part(s, rect(52, 72, 12, 16, 3), new Color(0x4A3A2A));
        part(s, rect(33, 84, 17, 8, 3), DARK_BROWN);
        part(s, rect(50, 84, 17, 8, 3), DARK_BROWN);
        part(s, oval(15, 38, 17, 30), vertical(38, light, 68, dark));
        part(s, rect(26, 34, 48, 42, 16), vertical(34, light, 76, dark));
        part(s, poly(28, 40, 36, 35, 72, 70, 64, 75), BROWN);
        fill(s, oval(48, 52, 6, 6), GOLD);
        part(s, oval(68, 38, 17, 30), vertical(38, light, 68, dark));
        part(s, oval(72, 60, 12, 10), dark);
        part(s, poly(44, 12, 50, 0, 56, 12), new Color(0x1C1C20));
        part(s, oval(34, 10, 32, 30), vertical(10, light, 40, dark));
        fill(s, rect(38, 19, 24, 4, 2), new Color(0x2C3A22));
        fill(s, oval(41, 23, 7, 5), new Color(0xE0352B));
        fill(s, oval(53, 23, 7, 5), new Color(0xE0352B));
        fill(s, rect(42, 32, 16, 3, 1), new Color(0x2A1414));
        part(s, poly(42, 34, 44, 26, 47, 34), Color.WHITE);
        part(s, poly(53, 34, 56, 26, 58, 34), Color.WHITE);
    }

    private static void dragon(Graphics2D s, boolean charging) {
        Color red = new Color(0xB8352C);
        Color deep = new Color(0x7A1E18);
        Color wing = new Color(0x5E1A14);
        part(s, poly(52, 40, 80, 0, 74, 24, 96, 20, 72, 44, 62, 50), vertical(0, new Color(0xA2342A), 50, wing));
        Path2D.Double tail = new Path2D.Double();
        tail.moveTo(32, 70);
        tail.quadTo(8, 88, 2, 62);
        tail.lineTo(8, 64);
        tail.quadTo(14, 78, 36, 62);
        tail.closePath();
        part(s, tail, vertical(60, red, 88, deep));
        part(s, poly(2, 62, -4, 52, 8, 58), BONE);
        part(s, oval(30, 74, 14, 16), deep);
        part(s, oval(58, 74, 14, 16), deep);
        part(s, oval(24, 44, 52, 40), vertical(44, red, 84, deep));
        part(s, oval(36, 54, 28, 28), vertical(54, new Color(0xF0B566), 82, new Color(0xC27A32)));
        if (charging) {
            fill(s, oval(30, 48, 40, 40), glow(50, 68, 22, new Color(255, 210, 90, 220), new Color(255, 120, 40, 0)));
        }
        part(s, poly(40, 40, 8, 4, 16, 30, 0, 34, 20, 48, 10, 58, 40, 56), vertical(4, new Color(0xA2342A), 58, wing));
        part(s, poly(58, 56, 64, 28, 76, 28, 70, 60), vertical(28, red, 60, deep));
        part(s, poly(66, 18, 59, 3, 71, 15), BONE);
        part(s, poly(73, 16, 74, 1, 80, 15), BONE);
        part(s, poly(62, 18, 84, 15, 99, 25, 97, 32, 82, 34, 63, 32), vertical(15, red, 34, deep));
        line(s, OUTLINE, 1.2f, 80, 29, 97, 29);
        fill(s, oval(73, 19, 7, 5), new Color(0xFFD84A));
        fill(s, oval(75.5, 19.5, 2, 4), OUTLINE);
        fill(s, oval(93, 23, 3, 2), OUTLINE);
        part(s, poly(86, 34, 88, 38, 90, 34), Color.WHITE);
        if (charging) {
            fill(s, oval(88, 18, 24, 22), glow(99, 29, 12, new Color(255, 220, 120, 230), new Color(255, 110, 30, 0)));
        }
    }

    private static void chest(Graphics2D s, long time) {
        fill(s, oval(14, 84, 72, 10), new Color(0, 0, 0, 100));
        part(s, rect(24, 56, 52, 32, 4), vertical(56, new Color(0x9A6232), 88, new Color(0x5E3A1C)));
        part(s, rect(24, 42, 52, 18, 12), vertical(42, new Color(0xB07440), 60, new Color(0x7A4A22)));
        fill(s, rect(24, 58, 52, 4, 1), GOLD);
        fill(s, rect(30, 42, 5, 46, 1), GOLD);
        fill(s, rect(65, 42, 5, 46, 1), GOLD);
        part(s, rect(45, 58, 10, 12, 2), GOLD);
        fill(s, oval(48.5, 62, 3, 4), OUTLINE);
        for (int i = 0; i < 5; i++) {
            double phase = (time / 400.0 + i * 1.3) % 3.0;
            if (phase < 1.0) {
                double sparkle = Math.sin(phase * Math.PI);
                double px = 26 + i * 12;
                double py = 38 - (i % 2) * 8;
                double r = 3 * sparkle;
                fill(s, poly(px, py - r * 2, px + r, py, px, py + r * 2, px - r, py), new Color(255, 236, 150, (int) (230 * sparkle)));
            }
        }
    }

    private static void spring(Graphics2D s, long time) {
        double pulse = 0.5 + 0.5 * Math.sin(time / 500.0);
        fill(s, oval(4, 30, 92, 64), glow(50, 70, 46, new Color(90, 220, 255, (int) (60 + 50 * pulse)), new Color(90, 220, 255, 0)));
        part(s, oval(16, 64, 68, 26), vertical(64, new Color(0x9AA0AA), 90, new Color(0x55596A)));
        fill(s, oval(22, 66, 56, 16), vertical(66, new Color(0x9BEFFF), 82, new Color(0x2E8FC6)));
        for (int i = 0; i < 6; i++) {
            double progress = ((time / 1400.0) + i / 6.0) % 1.0;
            double bx = 30 + i * 8 + Math.sin(time / 300.0 + i) * 2;
            double by = 74 - progress * 46;
            int alpha = (int) (200 * (1 - progress));
            fill(s, oval(bx, by, 4, 4), new Color(200, 245, 255, alpha));
        }
    }

    private static void door(Graphics2D s) {
        Area arch = new Area(new Rectangle2D.Double(28, 40, 44, 50));
        arch.add(new Area(new Ellipse2D.Double(28, 18, 44, 44)));
        Area frame = new Area(new Rectangle2D.Double(22, 40, 56, 50));
        frame.add(new Area(new Ellipse2D.Double(22, 12, 56, 56)));
        part(s, frame, vertical(12, new Color(0x6A6E7A), 90, new Color(0x3E414C)));
        part(s, arch, vertical(18, new Color(0x7A5030), 90, new Color(0x4A2E18)));
        s.setColor(new Color(0x3A2412));
        for (int x = 39; x < 72; x += 11) {
            s.draw(new Line2D.Double(x, 22, x, 90));
        }
        fill(s, rect(28, 50, 44, 4, 1), new Color(0x2E2E34));
        fill(s, rect(28, 74, 44, 4, 1), new Color(0x2E2E34));
        s.setStroke(new BasicStroke(2f));
        s.setColor(GOLD);
        s.draw(oval(60, 58, 7, 7));
        s.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }
}
