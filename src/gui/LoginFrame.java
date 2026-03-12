package gui;

import dao.AdminUserDAO;
import dao.UserDAO;
import models.AdminUser;
import models.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginFrame extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color BG       = new Color(13,  27,  42);
    static final Color CARD     = new Color(27,  42,  59);
    static final Color ACCENT   = new Color(79,  195, 247);
    static final Color SUCCESS  = new Color(76,  175, 80);
    static final Color DANGER   = new Color(244, 67,  54);
    static final Color TEXT     = new Color(255, 255, 255);
    static final Color SUBTEXT  = new Color(144, 164, 174);
    static final Color FIELD_BG = new Color(18,  35,  52);
    static final Color BORDER   = new Color(45,  65,  85);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> loginTypeBox;
    private JLabel         statusLabel;

    // Register fields
    private JTextField regName, regUser, regEmail, regPhone;
    private JPasswordField regPass, regConfirm;

    private JTabbedPane tabs;

    public LoginFrame() {
        setTitle("AutoPark — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG, getWidth(), getHeight(), new Color(10, 22, 35));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        root.add(buildLeft(),  BorderLayout.WEST);
        root.add(buildRight(), BorderLayout.CENTER);

        setContentPane(root);

        // Drag to move (undecorated window)
        addDragSupport(root);
    }

    // ── Left branding panel ──────────────────────────────────────────────────
    private JPanel buildLeft() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(21, 101, 192),
                        getWidth(), getHeight(), new Color(13, 71, 161));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circles
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(180, 400, 200, 200);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(100, 150, 300, 300);
            }
        };
        p.setPreferredSize(new Dimension(340, 0));
        p.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel emoji = new JLabel("🚗");
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        emoji.setAlignmentX(CENTER_ALIGNMENT);

        JLabel brand = new JLabel("AutoPark");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='text-align:center;'>Smart Parking<br>Management System</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(186, 214, 255));
        tagline.setAlignmentX(CENTER_ALIGNMENT);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(200, 1));

        JLabel[] features = {
                featureLbl("✅  Real-time slot tracking"),
                featureLbl("💳  Instant billing"),
                featureLbl("📊  Live dashboard"),
                featureLbl("🔐  Secure access control")
        };

        inner.add(Box.createVerticalStrut(20));
        inner.add(emoji);
        inner.add(Box.createVerticalStrut(12));
        inner.add(brand);
        inner.add(Box.createVerticalStrut(8));
        inner.add(tagline);
        inner.add(Box.createVerticalStrut(20));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(20));
        for (JLabel f : features) { inner.add(f); inner.add(Box.createVerticalStrut(10)); }

        p.add(inner);
        return p;
    }

    private JLabel featureLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(200, 225, 255));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    // ── Right form panel ─────────────────────────────────────────────────────
    private JPanel buildRight() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // Close button top-right
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeBtn.setForeground(SUBTEXT);
        closeBtn.setBackground(new Color(0,0,0,0));
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        topBar.add(closeBtn);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setForeground(TEXT);
        tabs.setBackground(CARD);
        tabs.setBorder(new EmptyBorder(0, 0, 0, 0));
        tabs.addTab("  Login  ",  buildLoginPanel());
        tabs.addTab("  Register  ", buildRegisterPanel());

        // Style tabs
        UIManager.put("TabbedPane.selected",    CARD);
        UIManager.put("TabbedPane.background",  BG);
        UIManager.put("TabbedPane.foreground",  TEXT);

        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(tabs,   BorderLayout.CENTER);

        JPanel padded = new JPanel(new BorderLayout());
        padded.setOpaque(false);
        padded.setBorder(new EmptyBorder(10, 30, 30, 30));
        padded.add(wrapper);
        return padded;
    }

    // ── Login tab ────────────────────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 0, 6, 0);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx   = 0;
        int row   = 0;

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        g.gridy = row++; g.insets = new Insets(0, 0, 4, 0);
        card.add(title, g);

        JLabel sub = new JLabel("Sign in to your AutoPark account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SUBTEXT);
        g.gridy = row++; g.insets = new Insets(0, 0, 20, 0);
        card.add(sub, g);

        g.insets = new Insets(5, 0, 2, 0);

        // Login type
        g.gridy = row++;
        card.add(fieldLabel("Login As"), g);
        loginTypeBox = new JComboBox<>(new String[]{"User", "Admin / Operator"});
        styleCombo(loginTypeBox);
        g.gridy = row++; g.insets = new Insets(2, 0, 10, 0);
        card.add(loginTypeBox, g);

        // Username
        g.insets = new Insets(5, 0, 2, 0);
        g.gridy = row++;
        card.add(fieldLabel("Username"), g);
        usernameField = new JTextField();
        styleField(usernameField, "Enter username");
        g.gridy = row++; g.insets = new Insets(2, 0, 10, 0);
        card.add(usernameField, g);

        // Password
        g.insets = new Insets(5, 0, 2, 0);
        g.gridy = row++;
        card.add(fieldLabel("Password"), g);
        passwordField = new JPasswordField();
        styleField(passwordField, "Enter password");
        g.gridy = row++; g.insets = new Insets(2, 0, 14, 0);
        card.add(passwordField, g);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(DANGER);
        g.gridy = row++; g.insets = new Insets(0, 0, 4, 0);
        card.add(statusLabel, g);

        // Login button
        JButton loginBtn = accentButton("Login →");
        loginBtn.addActionListener(e -> doLogin());
        g.gridy = row++; g.insets = new Insets(4, 0, 0, 0);
        card.add(loginBtn, g);

        // Enter key triggers login
        getRootPane().setDefaultButton(loginBtn);
        passwordField.addActionListener(e -> doLogin());

        p.add(card);
        return p;
    }

    // ── Register tab ─────────────────────────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx   = 0;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        card.add(title, g);

        JLabel sub = new JLabel("Register as a new AutoPark user");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SUBTEXT);
        g.gridy = 1; g.insets = new Insets(0, 0, 16, 0);
        card.add(sub, g);

        // Two-column grid for fields
        JPanel fields = new JPanel(new GridLayout(6, 2, 14, 10));
        fields.setOpaque(false);

        regName    = new JTextField(); styleField(regName,    "Full Name");
        regUser    = new JTextField(); styleField(regUser,    "Username");
        regEmail   = new JTextField(); styleField(regEmail,   "Email address");
        regPhone   = new JTextField(); styleField(regPhone,   "Phone number");
        regPass    = new JPasswordField(); styleField(regPass,    "Password");
        regConfirm = new JPasswordField(); styleField(regConfirm, "Confirm password");

        fields.add(withLabel("Full Name",  regName));
        fields.add(withLabel("Username",   regUser));
        fields.add(withLabel("Email",      regEmail));
        fields.add(withLabel("Phone",      regPhone));
        fields.add(withLabel("Password",   regPass));
        fields.add(withLabel("Confirm PW", regConfirm));

        g.gridy = 2; g.insets = new Insets(0, 0, 14, 0);
        card.add(fields, g);

        JLabel regStatus = new JLabel(" ");
        regStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        regStatus.setForeground(DANGER);
        g.gridy = 3; g.insets = new Insets(0, 0, 4, 0);
        card.add(regStatus, g);

        JButton regBtn = accentButton("Create Account →");
        regBtn.setBackground(SUCCESS);
        regBtn.addActionListener(e -> doRegister(regStatus));
        g.gridy = 4; g.insets = new Insets(0, 0, 0, 0);
        card.add(regBtn, g);

        p.add(card);
        return p;
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠  Please fill in all fields.");
            return;
        }

        String hash = sha256(password);
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(ACCENT);

        SwingWorker<Object, Void> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                if (loginTypeBox.getSelectedIndex() == 1) {
                    return AdminUserDAO.authenticate(username, hash);
                } else {
                    boolean valid = UserDAO.validateUser(username, hash);
                    return valid ? UserDAO.getUserByUsername(username) : null;
                }
            }

            @Override
            protected void done() {
                try {
                    Object result = get();
                    if (result instanceof AdminUser admin) {
                        AdminUserDAO.logActivity(admin.getId(), "Logged in via GUI");
                        dispose();
                        new AdminDashboard(admin).setVisible(true);
                    } else if (result instanceof User user) {
                        dispose();
                        new UserDashboard(user).setVisible(true);
                    } else {
                        statusLabel.setForeground(DANGER);
                        statusLabel.setText("✗  Invalid username or password.");
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(DANGER);
                    statusLabel.setText("✗  Login error. Try again.");
                }
            }
        };
        worker.execute();
    }

    private void doRegister(JLabel statusLbl) {
        String name    = regName.getText().trim();
        String user    = regUser.getText().trim();
        String email   = regEmail.getText().trim();
        String phone   = regPhone.getText().trim();
        String pass    = new String(regPass.getPassword());
        String confirm = new String(regConfirm.getPassword());

        if (name.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLbl.setForeground(DANGER);
            statusLbl.setText("⚠  Please fill in all fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            statusLbl.setForeground(DANGER);
            statusLbl.setText("⚠  Passwords do not match.");
            return;
        }

        statusLbl.setForeground(ACCENT);
        statusLbl.setText("Creating account...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                User newUser = new User(user, email, sha256(pass), name);
                newUser.setPhone(phone);
                return UserDAO.createUser(newUser);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        statusLbl.setForeground(SUCCESS);
                        statusLbl.setText("✅  Account created! You can now login.");
                        clearRegFields();
                        Timer t = new Timer(1500, ev -> tabs.setSelectedIndex(0));
                        t.setRepeats(false); t.start();
                    } else {
                        statusLbl.setForeground(DANGER);
                        statusLbl.setText("✗  Username or email already taken.");
                    }
                } catch (Exception ex) {
                    statusLbl.setForeground(DANGER);
                    statusLbl.setText("✗  Registration failed.");
                }
            }
        };
        worker.execute();
    }

    private void clearRegFields() {
        regName.setText(""); regUser.setText(""); regEmail.setText("");
        regPhone.setText(""); regPass.setText(""); regConfirm.setText("");
    }

    // ── Styling helpers ──────────────────────────────────────────────────────
    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(SUBTEXT);
        return l;
    }

    static void styleField(JComponent field, String placeholder) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT);
        if (field instanceof JTextField tf2) { tf2.setCaretColor(ACCENT); }
        if (field instanceof JPasswordField pf) { pf.setCaretColor(ACCENT); }
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(0, 36));
        if (field instanceof JTextField tf && !(field instanceof JPasswordField)) {
            tf.putClientProperty("JTextField.placeholderText", placeholder);
        }
    }

    static void styleCombo(JComboBox<?> box) {
        box.setBackground(FIELD_BG);
        box.setForeground(TEXT);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(0, 36));
        box.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(new Color(10, 30, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        btn.addMouseListener(new MouseAdapter() {
            final Color orig = btn.getBackground();
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(orig.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        return btn;
    }

    private JPanel withLabel(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        p.add(fieldLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Point dragStart;
    private void addDragSupport(JPanel root) {
        root.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { dragStart = e.getPoint(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragStart.x,
                            loc.y + e.getY() - dragStart.y);
                }
            }
        });
    }
}