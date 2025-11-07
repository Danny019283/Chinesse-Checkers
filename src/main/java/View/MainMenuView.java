package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuView extends JFrame {
    private final JButton btnStartGame = new JButton("Iniciar Juego");
    private final JButton btnViewStats = new JButton("Ver Estadísticas");
    private final JTextField txtIP = new JTextField(15);
    private final JTextField txtPort = new JTextField(6);
    private static MainMenuView instance;

    // Configuración fácil de ajustar
    private final Dimension buttonSize = new Dimension(300, 60);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 40);

    public static MainMenuView getInstance() {
        if (instance == null) {
            instance = new MainMenuView();
        }
        return instance;
    }

    public MainMenuView() {
        setTitle("Chinese Checkers - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(80, 80, 80, 80));
        mainPanel.setBackground(new Color(0xF5F5F5));

        // Panel del título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0xF5F5F5));
        JLabel lblTitle = new JLabel("CHINESE CHECKERS");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(0x6200EE));
        titlePanel.add(lblTitle);

        // Panel central con botones
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0xF5F5F5));

        // Panel de botones principales
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new EmptyBorder(40, 0, 40, 0));
        buttonPanel.setBackground(new Color(0xF5F5F5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Configurar botones (rectangulares y atractivos)
        configureButton(btnStartGame);
        configureButton(btnViewStats);

        buttonPanel.add(btnStartGame, gbc);
        buttonPanel.add(btnViewStats, gbc);

        // Panel de configuración de servidor
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));
        serverPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0x6200EE), 1),
                        "Configuración del Servidor"
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        serverPanel.setBackground(new Color(0xF5F5F5));
        serverPanel.setPreferredSize(new Dimension(250, 120));

        // Fuente para los títulos
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

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

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 8));
        portPanel.setBackground(new Color(0xF5F5F5));
        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setFont(labelFont);
        lblPort.setForeground(Color.BLACK);
        txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPort.setText("8080");
        txtPort.setPreferredSize(new Dimension(80, 25));
        portPanel.add(lblPort);
        portPanel.add(txtPort);

        serverPanel.add(ipPanel);
        serverPanel.add(portPanel);

        // Ensamblar la interfaz
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(serverPanel, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Establecer tamaño 800x700
        setSize(800, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Método para configurar botones de manera consistente
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

        // Efecto hover simple
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x3700B3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x6200EE));
            }
        });
    }

    // Métodos para obtener los datos del servidor
    public String getServerIP() {
        return txtIP.getText().trim();
    }

    public int getServerPort() {
        try {
            return Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            return 8080; // Valor por defecto
        }
    }

    // Métodos para agregar listeners (mismo patrón que la referencia)
    public void addStartGameListener(ActionListener accion) {
        btnStartGame.addActionListener(accion);
    }

    public void addViewStatsListener(ActionListener accion) {
        btnViewStats.addActionListener(accion);
    }

    // Método para mostrar mensajes de error/éxito
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Método para limpiar campos
    public void clearFields() {
        txtIP.setText("localhost");
        txtPort.setText("8080");
    }

    // Método para cerrar el menú
    public void closeMenu() {
        dispose();
        instance = null;
    }

    // Método estático para probar la vista
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuView menu = new MainMenuView();

            // Ejemplo de cómo ajustar el tamaño fácilmente
            // menu.setButtonSize(180, 35); // Para botones más pequeños
            // menu.setButtonSize(220, 45); // Para botones más grandes

            // Ejemplo de uso de listeners
            menu.addStartGameListener(e -> {
                String ip = menu.getServerIP();
                int port = menu.getServerPort();
                menu.showMessage("Conectando a " + ip + ":" + port, "Iniciando Juego", JOptionPane.INFORMATION_MESSAGE);
                menu.closeMenu();
            });

            menu.addViewStatsListener(e -> {
                menu.showMessage("Funcionalidad de estadísticas en desarrollo", "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
            });
        });
    }
}