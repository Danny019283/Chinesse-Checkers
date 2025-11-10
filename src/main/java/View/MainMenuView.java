package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuView extends JFrame {
    private final JButton btnStartGame = new JButton("Iniciar Juego");
    private final JButton btnViewStats = new JButton("Ver Estadísticas");
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtIP = new JTextField(15);
    private final JTextField txtPort = new JTextField(6);
    private final JComboBox<Integer> cmbPlayerCount = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6});

    private final Dimension buttonSize = new Dimension(300, 60);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 40);


    public MainMenuView() {
        setTitle("Chinese Checkers - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        buildUI();
        setSize(800, 550);
        setLocationRelativeTo(null);
    }

    public void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(80, 80, 80, 80));
        mainPanel.setBackground(new Color(0xF5F5F5));

        // title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0xF5F5F5));
        JLabel lblTitle = new JLabel("CHINESE CHECKERS");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(0x6200EE));
        titlePanel.add(lblTitle);

        // center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0xF5F5F5));

        // buttons panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new EmptyBorder(40, 0, 40, 0));
        buttonPanel.setBackground(new Color(0xF5F5F5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        configureButton(btnStartGame);
        configureButton(btnViewStats);
        buttonPanel.add(btnStartGame, gbc);
        buttonPanel.add(btnViewStats, gbc);

        // server configuration panel
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));
        serverPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0x6200EE), 1),
                        "Configuración de Partida"
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        serverPanel.setBackground(new Color(0xF5F5F5));
        serverPanel.setPreferredSize(new Dimension(250, 150));

        // title font
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        //name panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        namePanel.setBackground(new Color(0xF5F5F5));
        JLabel lblName = new JLabel("Tu Nombre:");
        lblName.setFont(labelFont);
        lblName.setForeground(Color.BLACK);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtName.setPreferredSize(new Dimension(120, 25));
        namePanel.add(lblName);
        namePanel.add(txtName);
        //num players panel
        JPanel playerCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        playerCountPanel.setBackground(new Color(0xF5F5F5));
        JLabel lblPlayerCount = new JLabel("Jugadores:");
        lblPlayerCount.setFont(labelFont);
        lblPlayerCount.setForeground(Color.BLACK);
        cmbPlayerCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbPlayerCount.setPreferredSize(new Dimension(80, 25));
        playerCountPanel.add(lblPlayerCount);
        playerCountPanel.add(cmbPlayerCount);
        //ip panel
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        ipPanel.setBackground(new Color(0xF5F5F5));
        JLabel lblIP = new JLabel("IP del Servidor:");
        lblIP.setFont(labelFont);
        lblIP.setForeground(Color.BLACK);
        txtIP.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtIP.setText("localhost");
        txtIP.setPreferredSize(new Dimension(120, 25));
        ipPanel.add(lblIP);
        ipPanel.add(txtIP);
        //port panel
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        portPanel.setBackground(new Color(0xF5F5F5));
        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setFont(labelFont);
        lblPort.setForeground(Color.BLACK);
        txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPort.setText("12345");
        txtPort.setPreferredSize(new Dimension(80, 25));
        portPanel.add(lblPort);
        portPanel.add(txtPort);

        serverPanel.add(namePanel);
        serverPanel.add(playerCountPanel);
        serverPanel.add(ipPanel);
        serverPanel.add(portPanel);


        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(serverPanel, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void configureButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(0x6200EE));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3700B3), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x3700B3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x6200EE));
            }
        });
    }

    // getters
    public String getPlayerName() {
        return txtName.getText().trim();
    }

    public String getServerIP() {
        return txtIP.getText().trim();
    }

    public int getSelectedPlayerCount() {
        return (Integer) cmbPlayerCount.getSelectedItem();
    }

    public int getServerPort() {
        try {
            return Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            return 8080; // Valor por defecto
        }
    }

    // listeners
    public void addStartGameListener(ActionListener accion) {
        btnStartGame.addActionListener(accion);
    }

    public void addViewStatsListener(ActionListener accion) {
        btnViewStats.addActionListener(accion);
    }

    // Joptions
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // functions
    public void clearFields() {
        txtIP.setText("localhost");
        txtPort.setText("8080");
    }

    public void closeMenu() {
        dispose();
    }

}