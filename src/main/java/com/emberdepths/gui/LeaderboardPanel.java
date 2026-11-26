package com.emberdepths.gui;

import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.service.ScoreService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;

public class LeaderboardPanel extends JPanel {

    private final GameWindow window;
    private final ScoreService scoreService;
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"Score", "Deepest floor", "Name"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"#", "Hero", "Class", "Level", "Floor", "Score", "Result"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public LeaderboardPanel(GameWindow window, ScoreService scoreService) {
        super(new BorderLayout(0, 18));
        this.window = window;
        this.scoreService = scoreService;
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(36, 60, 36, 60));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Hall of Fame");
        title.setFont(Theme.font(Font.BOLD, 40));
        title.setForeground(Theme.ACCENT);
        top.add(title, BorderLayout.WEST);
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        sortPanel.setOpaque(false);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(Theme.TEXT);
        sortLabel.setFont(Theme.font(Font.PLAIN, 16));
        sortBox.setFont(Theme.font(Font.PLAIN, 16));
        sortBox.setFocusable(false);
        sortBox.addActionListener(e -> reload());
        sortPanel.add(sortLabel);
        sortPanel.add(sortBox);
        top.add(sortPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setFont(Theme.font(Font.PLAIN, 16));
        table.setRowHeight(34);
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(Theme.PANEL_LIGHT);
        table.setSelectionForeground(Theme.TEXT);
        table.setFocusable(false);
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        for (int column : new int[]{0, 3, 4, 5, 6}) {
            table.getColumnModel().getColumn(column).setCellRenderer(centered);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.font(Font.BOLD, 16));
        header.setBackground(Theme.PANEL_LIGHT);
        header.setForeground(Theme.TEXT);
        header.setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.BACKGROUND);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        JButton back = Theme.button("Back to menu");
        back.addActionListener(e -> window.showMenu());
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    public void reload() {
        Comparator<ScoreEntry> order = switch (sortBox.getSelectedIndex()) {
            case 1 -> ScoreService.byDeepestFloor();
            case 2 -> ScoreService.byName();
            default -> ScoreService.byScore();
        };
        model.setRowCount(0);
        try {
            List<ScoreEntry> entries = scoreService.getLeaderboard(order);
            for (int i = 0; i < entries.size(); i++) {
                ScoreEntry e = entries.get(i);
                model.addRow(new Object[]{i + 1, e.getHeroName(), e.getHeroClass(), e.getLevel(), e.getFloor(),
                        e.getScore(), e.isVictory() ? "Victory" : "Fallen"});
            }
        } catch (PersistenceException e) {
            window.showError(e.getMessage());
        }
    }
}
