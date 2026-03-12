import gui.AppConfig;
import gui.LoginFrame;
import utils.DBConnection;
import utils.DBInitializer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class App {

    public static void main(String[] args) {
        // Set Nimbus look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JWindow splash = showSplash();

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    Connection conn = DBConnection.getConnection();
                    if (conn != null) {
                        DBInitializer.initializeSlots(AppConfig.TOTAL_SLOTS);
                        return true;
                    }
                    return false;
                }

                @Override
                protected void done() {
                    splash.dispose();
                    try {
                        if (get()) {
                            new LoginFrame().setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Cannot connect to database.\nCheck config/db.properties and ensure MySQL is running.",
                                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });
    }

    private static JWindow showSplash() {
        JWindow splash = new JWindow();
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(13, 27, 42),
                        getWidth(), getHeight(), new Color(21, 101, 192));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(420, 220));
        panel.setBorder(BorderFactory.createLineBorder(new Color(79, 195, 247), 2));

        JLabel icon  = new JLabel("🚗", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        JLabel title = new JLabel("AutoPark", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(79, 195, 247));
        JLabel sub   = new JLabel("Starting up, please wait...", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(144, 164, 174));

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 8));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(icon); center.add(title); center.add(sub);
        panel.add(center, BorderLayout.CENTER);
        splash.add(panel);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        return splash;
    }
}