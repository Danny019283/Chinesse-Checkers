package View;

import Model.Entities.GameStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameStatsView extends JFrame {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Buscar");
    private final JTable tblStats = new JTable();
    private final JScrollPane scrollPane = new JScrollPane(tblStats);
    private static GameStatsView instance;

    public static GameStatsView getInstance() {
        if (instance == null) {
            instance = new GameStatsView();
        }
        return instance;
    }

    public GameStatsView() {
        setTitle("Estadísticas - Chinese Checkers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        buildUI();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void buildUI() {
        //main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0xF5F5F5));

        //top panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(0xF5F5F5));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0x6200EE), 1),
                        "Buscar Juegos"
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        //config
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 30));
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch.setBackground(new Color(0x6200EE));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        configureTable();

        // center panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(0xF5F5F5));
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(0x6200EE), 1),
                        "Historial de Juegos"
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void configureTable() {
        String[] columnNames = {"ID Juego", "Ganador", "Color Ganador", "Jugadores"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        tblStats.setModel(model);
        tblStats.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblStats.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblStats.getTableHeader().setBackground(new Color(0x6200EE));
        tblStats.getTableHeader().setForeground(Color.WHITE);
        tblStats.setRowHeight(25);
        tblStats.setSelectionBackground(new Color(0xE3F2FD));
        tblStats.setSelectionForeground(Color.BLACK);
        tblStats.setGridColor(new Color(0xE0E0E0));
        tblStats.setShowGrid(true);


    }

    // Método para actualizar la tabla con datos de GameStats
    public void updateTable(ArrayList<GameStats> statsList) {
        DefaultTableModel model = (DefaultTableModel) tblStats.getModel();
        model.setRowCount(0); // Limpiar tabla

        for (GameStats stats : statsList) {
            String players = String.join(", ", stats.getNamePlayers());
            model.addRow(new Object[]{
                    stats.getGameId(),
                    stats.getWinner(),
                    stats.getWinnerColor(),
                    players
            });
        }
    }

    // Método para obtener el ID del juego seleccionado
    public int getSelectedGameId() {
        int selectedRow = tblStats.getSelectedRow();
        if (selectedRow != -1) {
            return (int) tblStats.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    // Método para obtener la fila seleccionada completa
    public Object[] getSelectedGameData() {
        int selectedRow = tblStats.getSelectedRow();
        if (selectedRow != -1) {
            Object[] rowData = new Object[tblStats.getColumnCount()];
            for (int i = 0; i < tblStats.getColumnCount(); i++) {
                rowData[i] = tblStats.getValueAt(selectedRow, i);
            }
            return rowData;
        }
        return null;
    }

    // Métodos para los listeners
    public void addSearchListener(ActionListener accion) {
        btnSearch.addActionListener(accion);
    }

    public void addTableSelectionListener(javax.swing.event.ListSelectionListener accion) {
        tblStats.getSelectionModel().addListSelectionListener(accion);
    }

    // Métodos para obtener datos de la UI
    public String getSearchText() {
        return txtSearch.getText().trim();
    }

    public JTable getStatsTable() {
        return tblStats;
    }

    // Método para mostrar detalles del juego seleccionado
    public void showGameDetails(GameStats stats) {
        if (stats != null) {
            String message = String.format(
                    "ID del Juego: %d\nGanador: %s\nColor Ganador: %s\nJugadores: %s",
                    stats.getGameId(),
                    stats.getWinner(),
                    stats.getWinnerColor(),
                    String.join(", ", stats.getNamePlayers())
            );
            JOptionPane.showMessageDialog(this, message, "Detalles del Juego", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método para mostrar detalles del juego seleccionado desde la tabla
    public void showSelectedGameDetails() {
        Object[] selectedData = getSelectedGameData();
        if (selectedData != null) {
            String message = String.format(
                    "ID del Juego: %s\nGanador: %s\nColor Ganador: %s\nJugadores: %s",
                    selectedData[0],
                    selectedData[1],
                    selectedData[2],
                    selectedData[3]
            );
            JOptionPane.showMessageDialog(this, message, "Detalles del Juego", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método para cerrar la ventana
    public void closeView() {
        dispose();
        instance = null;
    }

    // Método estático para probar la vista (ahora requiere la clase GameStats externa)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameStatsView statsView = new GameStatsView();

            // Listeners de ejemplo
            statsView.addSearchListener(e -> {
                String searchText = statsView.getSearchText();
                statsView.showMessage("Buscando: " + searchText, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            });

            statsView.addTableSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    statsView.showSelectedGameDetails();
                }
            });
        });
    }

    // Método para mostrar mensajes
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}