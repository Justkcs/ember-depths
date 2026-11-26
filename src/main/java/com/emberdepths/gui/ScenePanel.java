package com.emberdepths.gui;

import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.domain.world.Room;
import com.emberdepths.domain.world.RoomType;
import com.emberdepths.service.GameService;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ScenePanel extends JPanel {

    private static final int FRAME_MS = 33;
    private static final long FLOAT_DURATION = 1100;
    private static final long FLASH_DURATION = 300;
    private static final long FADE_DURATION = 900;
    private static final long APPEAR_DURATION = 500;

    private final List<FloatingText> floaters = new ArrayList<>();
    private final Timer timer;
    private GameService game;
    private long heroFlashStart = -1;
    private long enemyFlashStart = -1;
    private Monster fadingEnemy;
    private long fadeStart;
    private Monster shownEnemy;
    private long appearStart;

    public ScenePanel() {
        setPreferredSize(new Dimension(1100, 440));
        setBackground(Theme.BACKGROUND);
        timer = new Timer(FRAME_MS, e -> {
            long now = now();
            floaters.removeIf(f -> now - f.start() > FLOAT_DURATION);
            repaint();
        });
    }

    public void setGame(GameService game) {
        this.game = game;
        floaters.clear();
        fadingEnemy = null;
        shownEnemy = null;
        heroFlashStart = -1;
        enemyFlashStart = -1;
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void enemyHit(int amount) {
        long start = now();
        enemyFlashStart = start;
        floaters.add(new FloatingText("-" + amount, Theme.ACCENT, false, start));
    }

    public void heroHit(int amount, long delay) {
        long start = now() + delay;
        heroFlashStart = start;
        floaters.add(new FloatingText("-" + amount, Theme.HEALTH, true, start));
    }

    public void heroHealed(int amount) {
        floaters.add(new FloatingText("+" + amount, Theme.HEAL, true, now()));
    }

    public void heroMessage(String text, Color color) {
        floaters.add(new FloatingText(text, color, true, now()));
    }

    public void enemyDefeated(Monster monster) {
        fadingEnemy = monster;
        fadeStart = now() + 250;
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        long t = now();
        int floor = game == null ? 1 : game.getState().getDungeon().getCurrentFloor();
        paintBackground(g, w, h, floor, t);
        if (game != null) {
            paintActors(g, w, h, t);
            paintHeader(g, w);
            paintHeroCard(g, game.getState().getHero());
            paintEnemyCard(g, w);
            paintFloaters(g, w, h, t);
            paintOverlay(g, w, h);
        }
        g.dispose();
    }

    private void paintBackground(Graphics2D g, int w, int h, int floor, long t) {
        double depth = Math.min(1.0, (floor - 1) / 9.0);
        Color wallTop = Theme.lerp(new Color(0x2E3244), new Color(0x40201C), depth);
        Color wallBottom = Theme.lerp(new Color(0x1C1F2B), new Color(0x26120F), depth);
        int wallHeight = (int) (h * 0.70);
        g.setPaint(new GradientPaint(0, 0, wallTop, 0, wallHeight, wallBottom));
        g.fillRect(0, 0, w, wallHeight);
        int brickHeight = 26;
        int brickWidth = 62;
        for (int row = 0; row * brickHeight < wallHeight; row++) {
            int offset = (row % 2) * brickWidth / 2;
            for (int col = -1; col * brickWidth < w + brickWidth; col++) {
                int bx = col * brickWidth + offset;
                int by = row * brickHeight;
                int shade = Math.floorMod(row * 73 + col * 37 + floor * 11, 7) - 3;
                Color base = Theme.lerp(wallTop, wallBottom, (double) by / wallHeight);
                g.setColor(Theme.shift(base, shade * 4 + 6));
                g.fillRect(bx + 2, by + 2, brickWidth - 4, Math.min(brickHeight - 4, wallHeight - by - 2));
            }
        }
        Color floorTop = Theme.lerp(new Color(0x2A2622), new Color(0x2E1612), depth);
        g.setPaint(new GradientPaint(0, wallHeight, floorTop, 0, h, new Color(0x0E0E12)));
        g.fillRect(0, wallHeight, w, h - wallHeight);
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, wallHeight, w, 6);
        g.setColor(new Color(255, 255, 255, 14));
        for (int i = -8; i <= 8; i++) {
            g.drawLine(w / 2 + i * 70, wallHeight + 6, w / 2 + i * 210, h);
        }
        paintTorch(g, (int) (w * 0.06), (int) (wallHeight * 0.58), t, 0);
        paintTorch(g, (int) (w * 0.94), (int) (wallHeight * 0.58), t, 2);
        float radius = Math.max(w, h) * 0.8f;
        g.setPaint(new RadialGradientPaint(new Point2D.Float(w / 2f, h * 0.55f), radius,
                new float[]{0.45f, 1f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 210)}));
        g.fillRect(0, 0, w, h);
    }

    private void paintTorch(Graphics2D g, int x, int y, long t, int seed) {
        float flicker = (float) (0.85 + 0.12 * Math.sin(t / 90.0 + seed) + 0.05 * Math.sin(t / 37.0 + seed * 3));
        g.setPaint(new RadialGradientPaint(new Point2D.Float(x, y - 10), 170 * flicker,
                new float[]{0f, 1f}, new Color[]{new Color(255, 150, 60, 85), new Color(255, 150, 60, 0)}));
        g.fillOval(x - 180, y - 190, 360, 360);
        g.setColor(new Color(0x3A3A40));
        g.fillRect(x - 4, y, 8, 36);
        g.fillRect(x - 12, y - 4, 24, 8);
        double flameHeight = 34 * flicker;
        g.setColor(Theme.ACCENT);
        g.fill(new Ellipse2D.Double(x - 10, y - 4 - flameHeight, 20, flameHeight + 6));
        g.setColor(new Color(0xFFD35C));
        g.fill(new Ellipse2D.Double(x - 5, y - 4 - flameHeight * 0.6, 10, flameHeight * 0.6 + 4));
    }

    private double heroSize(int h) {
        return h * 0.5;
    }

    private double heroX(int w) {
        return w * 0.17;
    }

    private double enemySize(int h, Monster monster) {
        return monster.isBoss() ? h * 0.78 : h * 0.5;
    }

    private double enemyCenterX(int w) {
        return w * 0.68;
    }

    private void paintActors(Graphics2D g, int w, int h, long t) {
        int groundY = (int) (h * 0.94);
        Hero hero = game.getState().getHero();
        double size = heroSize(h);
        double bob = Math.sin(t / 420.0) * 3;
        double heroShake = flashing(heroFlashStart, t) ? Math.sin(t / 14.0) * 7 : 0;
        if (flashing(heroFlashStart, t)) {
            paintHitGlow(g, heroX(w) + size / 2, groundY - size / 2, size);
        }
        float heroAlpha = hero.isAlive() ? 1f : 0.35f;
        SpriteRenderer.drawHero(g, hero.getHeroClass(), heroX(w) + heroShake, groundY - size + bob, size, heroAlpha);

        Monster enemy = game.getCurrentEnemy();
        if (enemy != shownEnemy) {
            shownEnemy = enemy;
            appearStart = t;
        }
        if (enemy != null) {
            double enemySize = enemySize(h, enemy);
            double shake = flashing(enemyFlashStart, t) ? Math.sin(t / 14.0) * 7 : 0;
            double enemyBob = Math.sin(t / 380.0 + 1.5) * 3;
            float alpha = (float) Math.min(1.0, (t - appearStart) / (double) APPEAR_DURATION);
            if (flashing(enemyFlashStart, t)) {
                paintHitGlow(g, enemyCenterX(w), groundY - enemySize / 2, enemySize);
            }
            SpriteRenderer.drawMonster(g, enemy, enemyCenterX(w) - enemySize / 2 + shake,
                    groundY - enemySize + enemyBob, enemySize, alpha);
            return;
        }
        if (fadingEnemy != null && t - fadeStart < FADE_DURATION) {
            double enemySize = enemySize(h, fadingEnemy);
            double progress = Math.max(0, (t - fadeStart) / (double) FADE_DURATION);
            SpriteRenderer.drawMonster(g, fadingEnemy, enemyCenterX(w) - enemySize / 2,
                    groundY - enemySize + progress * 40, enemySize, (float) (1 - progress));
            return;
        }
        RoomType type = game.getState().getDungeon().getCurrentRoom().map(Room::getType).orElse(RoomType.EMPTY);
        double featureSize = h * 0.42;
        SpriteRenderer.drawRoomFeature(g, type, enemyCenterX(w) - featureSize / 2, groundY - featureSize, featureSize, t);
    }

    private boolean flashing(long start, long t) {
        return start >= 0 && t >= start && t - start < FLASH_DURATION;
    }

    private void paintHitGlow(Graphics2D g, double cx, double cy, double size) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) (size * 0.6),
                new float[]{0f, 1f}, new Color[]{new Color(255, 60, 40, 110), new Color(255, 60, 40, 0)}));
        g.fill(new Ellipse2D.Double(cx - size * 0.6, cy - size * 0.6, size * 1.2, size * 1.2));
    }

    private void paintHeader(Graphics2D g, int w) {
        Dungeon dungeon = game.getState().getDungeon();
        String title = "FLOOR " + dungeon.getCurrentFloor() + " / " + dungeon.getTotalFloors();
        String subtitle = dungeon.getCurrentRoom()
                .map(room -> "Room " + room.getNumber() + "  ·  " + capitalize(room.getType().getDescription()))
                .orElse("The entrance");
        g.setFont(Theme.font(Font.BOLD, 26));
        g.setColor(Theme.ACCENT);
        Theme.drawCentered(g, title, w / 2, 42);
        g.setFont(Theme.font(Font.PLAIN, 16));
        g.setColor(Theme.TEXT);
        Theme.drawCentered(g, subtitle, w / 2, 66);
    }

    private static String capitalize(String text) {
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private void card(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(13, 15, 22, 200));
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(255, 255, 255, 30));
        g.drawRoundRect(x, y, width, height, 16, 16);
    }

    private void paintHeroCard(Graphics2D g, Hero hero) {
        int x = 18;
        int y = 18;
        int width = 330;
        card(g, x, y, width, 132);
        g.setFont(Theme.font(Font.BOLD, 20));
        g.setColor(Theme.TEXT);
        g.drawString(hero.getName(), x + 16, y + 30);
        g.setFont(Theme.font(Font.BOLD, 14));
        g.setColor(Theme.ACCENT);
        String style = hero.getStrategy().getName();
        g.drawString(style, x + width - 16 - g.getFontMetrics().stringWidth(style), y + 28);
        g.setFont(Theme.font(Font.PLAIN, 14));
        g.setColor(Theme.MUTED);
        g.drawString(hero.getTypeName() + "  ·  Level " + hero.getLevel() + "  ·  " + hero.getGold() + " gold", x + 16, y + 50);
        Theme.drawBar(g, x + 16, y + 62, width - 32, 18, hero.getHealth(), hero.getMaxHealth(), Theme.HEALTH,
                "HP " + hero.getHealth() + " / " + hero.getMaxHealth());
        Theme.drawBar(g, x + 16, y + 86, width - 32, 18, hero.getMana(), hero.getMaxMana(), Theme.MANA,
                "MP " + hero.getMana() + " / " + hero.getMaxMana());
        Theme.drawBar(g, x + 16, y + 112, width - 32, 8, hero.getExperience(), hero.getExperienceToNextLevel(),
                Theme.EXPERIENCE, null);
    }

    private void paintEnemyCard(Graphics2D g, int w) {
        Monster enemy = game.getCurrentEnemy();
        if (enemy == null) {
            return;
        }
        int width = 300;
        int x = w - width - 18;
        int y = 18;
        card(g, x, y, width, 84);
        g.setFont(Theme.font(Font.BOLD, 20));
        g.setColor(enemy.isBoss() ? Theme.ACCENT : Theme.TEXT);
        g.drawString(enemy.getName(), x + 16, y + 30);
        if (enemy.isCharging()) {
            g.setFont(Theme.font(Font.BOLD, 13));
            g.setColor(new Color(0xFFD35C));
            String warning = "CHARGING FIRE";
            g.drawString(warning, x + width - 16 - g.getFontMetrics().stringWidth(warning), y + 28);
        }
        Theme.drawBar(g, x + 16, y + 48, width - 32, 20, enemy.getHealth(), enemy.getMaxHealth(), Theme.HEALTH,
                enemy.getHealth() + " / " + enemy.getMaxHealth());
    }

    private void paintFloaters(Graphics2D g, int w, int h, long t) {
        int groundY = (int) (h * 0.94);
        g.setFont(Theme.font(Font.BOLD, 30));
        for (FloatingText floater : floaters) {
            if (t < floater.start()) {
                continue;
            }
            double progress = (t - floater.start()) / (double) FLOAT_DURATION;
            double x;
            double y;
            if (floater.onHero()) {
                x = heroX(w) + heroSize(h) / 2;
                y = groundY - heroSize(h) - 6;
            } else {
                Monster reference = shownEnemy != null ? shownEnemy : fadingEnemy;
                double size = reference == null ? heroSize(h) : enemySize(h, reference);
                x = enemyCenterX(w);
                y = groundY - size - 6;
            }
            y -= progress * 60;
            int alpha = (int) (255 * Math.max(0, 1 - progress));
            int textX = (int) x - g.getFontMetrics().stringWidth(floater.text()) / 2;
            g.setColor(new Color(0, 0, 0, alpha));
            g.drawString(floater.text(), textX + 2, (int) y + 2);
            Color c = floater.color();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g.drawString(floater.text(), textX, (int) y);
        }
    }

    private void paintOverlay(Graphics2D g, int w, int h) {
        if (!game.isGameOver()) {
            return;
        }
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, w, h);
        boolean victory = game.isVictory();
        g.setFont(Theme.font(Font.BOLD, 64));
        g.setColor(victory ? new Color(0xFFD35C) : Theme.HEALTH);
        Theme.drawCentered(g, victory ? "VICTORY" : "YOU HAVE FALLEN", w / 2, h / 2);
        g.setFont(Theme.font(Font.PLAIN, 20));
        g.setColor(Theme.TEXT);
        Theme.drawCentered(g, "Score " + game.calculateScore() + "  ·  press Continue", w / 2, h / 2 + 44);
    }

    private record FloatingText(String text, Color color, boolean onHero, long start) {
    }
}
