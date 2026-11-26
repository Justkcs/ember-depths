package com.emberdepths.gui;

import com.emberdepths.domain.monster.Dragon;
import com.emberdepths.domain.monster.Monster;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class MenuPanel extends JPanel {

    private static final int EMBERS = 46;

    private final Monster dragon = new Dragon();
    private final JButton continueButton;
    private final Timer timer;

    public MenuPanel(GameWindow window) {
        super(new GridBagLayout());
        setBackground(Theme.BACKGROUND);
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.add(Box.createVerticalStrut(230));
        column.add(menuButton("New adventure", window::showCreateHero));
        column.add(Box.createVerticalStrut(14));
        continueButton = menuButton("Continue saved adventure", window::continueAdventure);
        column.add(continueButton);
        column.add(Box.createVerticalStrut(14));
        column.add(menuButton("Hall of Fame", window::showLeaderboard));
        column.add(Box.createVerticalStrut(14));
        column.add(menuButton("Exit", () -> System.exit(0)));
        add(column);
        timer = new Timer(40, e -> repaint());
    }

    private JButton menuButton(String text, Runnable action) {
        JButton button = Theme.button(text);
        button.setFont(Theme.font(Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(320, 50);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.addActionListener(e -> action.run());
        return button;
    }

    public void refresh(boolean hasSave) {
        continueButton.setEnabled(hasSave);
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        long t = System.currentTimeMillis();
        g.setPaint(new GradientPaint(0, 0, new Color(0x1C1A24), 0, h, new Color(0x0C0C10)));
        g.fillRect(0, 0, w, h);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(w * 0.5f, h * 1.05f), h * 0.9f,
                new float[]{0f, 1f}, new Color[]{new Color(240, 122, 58, 120), new Color(240, 122, 58, 0)}));
        g.fillRect(0, 0, w, h);
        double dragonSize = h * 0.62;
        SpriteRenderer.drawMonster(g, dragon, w - dragonSize * 0.95, h - dragonSize * 1.02, dragonSize, 0.22f);
        for (int i = 0; i < EMBERS; i++) {
            double speed = 18 + (i % 5) * 6;
            double y = h - ((t / speed + i * 53) % (h + 40));
            double x = (i * 97) % Math.max(1, w) + Math.sin(t / 900.0 + i) * 24;
            double size = 2 + (i % 3);
            int alpha = (int) (80 + 120 * (y / Math.max(1, h)));
            g.setColor(new Color(255, 140 + (i % 4) * 20, 60, Math.max(0, Math.min(255, alpha))));
            g.fill(new Ellipse2D.Double(x, y, size, size));
        }
        g.setFont(Theme.font(Font.BOLD, 80));
        g.setColor(new Color(240, 122, 58, 70));
        Theme.drawCentered(g, "EMBER DEPTHS", w / 2 + 3, 170 + 4);
        g.setColor(Theme.ACCENT);
        Theme.drawCentered(g, "EMBER DEPTHS", w / 2, 170);
        g.setFont(Theme.font(Font.PLAIN, 20));
        g.setColor(Theme.TEXT);
        Theme.drawCentered(g, "Descend ten floors. Defeat the Ember Dragon.", w / 2, 212);
        g.dispose();
    }
}
