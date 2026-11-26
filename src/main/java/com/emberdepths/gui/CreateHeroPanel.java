package com.emberdepths.gui;

import com.emberdepths.domain.hero.HeroClass;
import com.emberdepths.factory.HeroFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CreateHeroPanel extends JPanel {

    private final GameWindow window;
    private final HeroFactory heroFactory = new HeroFactory();
    private final List<ClassCard> cards = new ArrayList<>();
    private final JTextField nameField = new JTextField(16);
    private final JLabel errorLabel = new JLabel(" ");
    private HeroClass selected = HeroClass.WARRIOR;

    public CreateHeroPanel(GameWindow window) {
        super(new BorderLayout(0, 20));
        this.window = window;
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        JLabel title = new JLabel("Choose your hero", SwingConstants.CENTER);
        title.setFont(Theme.font(Font.BOLD, 40));
        title.setForeground(Theme.ACCENT);
        add(title, BorderLayout.NORTH);

        JPanel cardRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 10));
        cardRow.setOpaque(false);
        for (HeroClass heroClass : HeroClass.values()) {
            ClassCard card = new ClassCard(heroClass);
            cards.add(card);
            cardRow.add(card);
        }
        add(cardRow, BorderLayout.CENTER);
        add(createForm(), BorderLayout.SOUTH);
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setOpaque(false);
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        row.setOpaque(false);
        JLabel nameLabel = new JLabel("Hero name:");
        nameLabel.setForeground(Theme.TEXT);
        nameLabel.setFont(Theme.font(Font.PLAIN, 18));
        nameField.setFont(Theme.font(Font.PLAIN, 18));
        nameField.setBackground(Theme.PANEL_LIGHT);
        nameField.setForeground(Theme.TEXT);
        nameField.setCaretColor(Theme.TEXT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        nameField.addActionListener(e -> start());
        JButton startButton = Theme.button("Enter the depths");
        startButton.addActionListener(e -> start());
        JButton backButton = Theme.button("Back");
        backButton.addActionListener(e -> window.showMenu());
        row.add(nameLabel);
        row.add(nameField);
        row.add(startButton);
        row.add(backButton);
        errorLabel.setForeground(new Color(0xFF8A65));
        errorLabel.setFont(Theme.font(Font.PLAIN, 16));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(row, BorderLayout.CENTER);
        form.add(errorLabel, BorderLayout.SOUTH);
        return form;
    }

    public void reset() {
        nameField.setText("");
        errorLabel.setText(" ");
        select(HeroClass.WARRIOR);
        nameField.requestFocusInWindow();
    }

    private void select(HeroClass heroClass) {
        selected = heroClass;
        cards.forEach(ClassCard::repaint);
    }

    private void start() {
        try {
            window.beginAdventure(heroFactory.create(selected, nameField.getText()));
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private final class ClassCard extends JPanel {

        private final HeroClass heroClass;

        private ClassCard(HeroClass heroClass) {
            this.heroClass = heroClass;
            setPreferredSize(new Dimension(290, 330));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    select(heroClass);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            boolean active = heroClass == selected;
            g.setColor(active ? new Color(0x2C2433) : Theme.PANEL);
            g.fillRoundRect(2, 2, w - 4, h - 4, 22, 22);
            g.setColor(active ? Theme.ACCENT : Theme.BORDER);
            g.setStroke(new java.awt.BasicStroke(active ? 4f : 2f));
            g.drawRoundRect(2, 2, w - 4, h - 4, 22, 22);
            SpriteRenderer.drawHero(g, heroClass, (w - 190) / 2.0, 14, 190, 1f);
            g.setFont(Theme.font(Font.BOLD, 26));
            g.setColor(active ? Theme.ACCENT : Theme.TEXT);
            Theme.drawCentered(g, heroClass.getDisplayName(), w / 2, 238);
            g.setFont(Theme.font(Font.PLAIN, 16));
            g.setColor(Theme.MUTED);
            Theme.drawWrapped(g, heroClass.getDescription(), 22, 270, w - 44, 22);
            g.dispose();
        }
    }
}
