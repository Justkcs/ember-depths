package com.emberdepths.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class Theme {

    public static final Color BACKGROUND = new Color(0x14161F);
    public static final Color PANEL = new Color(0x1F2333);
    public static final Color PANEL_LIGHT = new Color(0x2A2F44);
    public static final Color BORDER = new Color(0x3A3F55);
    public static final Color LOG_BACKGROUND = new Color(0x0D0F16);
    public static final Color TEXT = new Color(0xECE6DA);
    public static final Color MUTED = new Color(0x9A958C);
    public static final Color ACCENT = new Color(0xF07A3A);
    public static final Color HEALTH = new Color(0xD9483B);
    public static final Color MANA = new Color(0x4A86E8);
    public static final Color EXPERIENCE = new Color(0xE8C547);
    public static final Color HEAL = new Color(0x5CCB6E);
    public static final String FONT_NAME = "Segoe UI";

    private Theme() {
    }

    public static Font font(int style, float size) {
        return new Font(FONT_NAME, style, Math.round(size));
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 16));
        button.setForeground(TEXT);
        button.setBackground(PANEL_LIGHT);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(9, 20, 9, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(0x5A3424));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PANEL_LIGHT);
            }
        });
        return button;
    }

    public static void drawBar(Graphics2D g, int x, int y, int width, int height,
                               int current, int max, Color color, String label) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, height, height);
        double ratio = max <= 0 ? 0 : Math.max(0, Math.min(1.0, (double) current / max));
        int filled = (int) Math.round(width * ratio);
        if (filled > 0) {
            g.setPaint(new GradientPaint(x, y, color.brighter(), x, y + height, color.darker()));
            g.fillRoundRect(x, y, filled, height, height, height);
        }
        g.setColor(new Color(255, 255, 255, 50));
        g.drawRoundRect(x, y, width, height, height, height);
        if (label != null) {
            g.setFont(font(Font.BOLD, height * 0.72f));
            FontMetrics metrics = g.getFontMetrics();
            int textX = x + (width - metrics.stringWidth(label)) / 2;
            int textY = y + (height + metrics.getAscent() - metrics.getDescent()) / 2;
            g.setColor(new Color(0, 0, 0, 160));
            g.drawString(label, textX + 1, textY + 1);
            g.setColor(TEXT);
            g.drawString(label, textX, textY);
        }
    }

    public static void drawCentered(Graphics2D g, String text, int centerX, int baselineY) {
        g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, baselineY);
    }

    public static int drawWrapped(Graphics2D g, String text, int x, int y, int width, int lineHeight) {
        FontMetrics metrics = g.getFontMetrics();
        StringBuilder line = new StringBuilder();
        int currentY = y;
        for (String word : text.split(" ")) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (metrics.stringWidth(candidate) > width && line.length() > 0) {
                g.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            g.drawString(line.toString(), x, currentY);
            currentY += lineHeight;
        }
        return currentY;
    }

    public static Color lerp(Color a, Color b, double t) {
        double clamped = Math.max(0, Math.min(1, t));
        return new Color(
                (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * clamped),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * clamped),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * clamped));
    }

    public static Color shift(Color color, int amount) {
        return new Color(
                clamp(color.getRed() + amount),
                clamp(color.getGreen() + amount),
                clamp(color.getBlue() + amount));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
