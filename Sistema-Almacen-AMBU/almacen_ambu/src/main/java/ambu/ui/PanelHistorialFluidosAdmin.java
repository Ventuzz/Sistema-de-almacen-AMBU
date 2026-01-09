package ambu.ui;

import ambu.mysql.DatabaseConnection;
import ambu.process.FluidosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/*-----------------------------------------------
    Panel de historial de Aceites y Anticongelantes
 -----------------------------------------------*/

public class PanelHistorialFluidosAdmin extends JPanel {

    private JTable table;
    private HistorialModel model;
    private TableRowSorter<HistorialModel> sorter;
    private JButton btnExportar;
    private JButton btnRefrescar;
    private JButton btnDevolver;
    private JTextField txtBuscar;

    private final FluidosService service = new FluidosService();

    public PanelHistorialFluidosAdmin(){
        buildUI();
        cargar();
    }
/*-----------------------------------------------
    Ensamble de la ventana
 -----------------------------------------------*/
    private void buildUI(){
        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));
        JLabel t = new JLabel("Historial de Aceites y Anticongelantes", SwingConstants.CENTER);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        add(t, BorderLayout.NORTH);

        model = new HistorialModel();
        table = new JTable(model);
        table.setDefaultRenderer(java.util.Date.class, new javax.swing.table.DefaultTableCellRenderer() {
            private final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

            @Override
            public void setValue(Object value) {
                try {
                    if (value instanceof java.util.Date) {
                        value = sdf.format(value);
                    }
                } catch (Exception e) { } // Ignorar errores de formato
                super.setValue(value);
            }
        });
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validarBotonDevolucion();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        javax.swing.JPanel barraBusqueda = new javax.swing.JPanel(new java.awt.BorderLayout(6,6));
        txtBuscar = new javax.swing.JTextField();
        txtBuscar.setPreferredSize(new java.awt.Dimension(200, 20));
        barraBusqueda.add(new javax.swing.JLabel("Buscar:"), java.awt.BorderLayout.WEST);
        barraBusqueda.add(txtBuscar, java.awt.BorderLayout.CENTER);
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltroAceites(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltroAceites(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltroAceites(); }
        });
        south.add(barraBusqueda);
        btnRefrescar = new JButton(new AbstractAction("Refrescar"){
            @Override public void actionPerformed(java.awt.event.ActionEvent e){ cargar(); }
        });
        
        // Atajo del teclado
                getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refreshF5");
                    getActionMap().put("refreshF5", new AbstractAction() {
                        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                            cargar();
                        }
                    });
        
        btnExportar = new JButton(new AbstractAction("Exportar CSV"){
            @Override public void actionPerformed(java.awt.event.ActionEvent e){ exportarCSV(); }
        });
        btnDevolver = new JButton(new AbstractAction("Registrar devolución"){
            @Override public void actionPerformed(java.awt.event.ActionEvent e){ onDevolver(); }
        });
        btnDevolver.setEnabled(false);
        south.add(btnRefrescar); south.add(btnExportar); south.add(btnDevolver);
        add(south, BorderLayout.SOUTH);
    }

    private void cargar(){
        new SwingWorker<java.util.List<HistorialRow>, Void>(){
            protected java.util.List<HistorialRow> doInBackground() throws Exception { return fetch(); }
            protected void done(){ try { model.setData(get()); validarBotonDevolucion(); } catch(Exception ex){ JOptionPane.showMessageDialog(PanelHistorialFluidosAdmin.this, ex.getMessage()); }}
        }.execute();
    }

    private List<HistorialRow> fetch() throws SQLException {
        final String SQL =
            "SELECT c.id_control_fluido, c.fecha, COALESCE(c.estado,'PENDIENTE'), " +
            "COALESCE(u.nom_usuario, c.solicitante_externo) AS solicitante, e.articulo AS fluido, " +
            "c.cantidad_entregada, c.unidad_entregada, c.cantidad_devuelta, c.unidad_devuelta, c.placas " +
            "FROM control_fluidos c " +
            "LEFT JOIN usuarios u ON u.usuario_id = c.id_usuario_solicitante " +
            "LEFT JOIN existencias e ON e.id = c.id_existencia_fluido " +
            "ORDER BY c.fecha DESC";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            List<HistorialRow> out = new ArrayList<HistorialRow>();
            Calendar cal = Calendar.getInstance();
            while (rs.next()) {
                HistorialRow r = new HistorialRow();
                r.id = rs.getInt(1);
                Timestamp t = rs.getTimestamp(2, cal);
                r.fecha = (t == null) ? null : new Date(t.getTime());
                r.estado = rs.getString(3);
                r.solicitante = rs.getString(4);
                r.fluido = rs.getString(5);
                r.cantEnt = rs.getBigDecimal(6);
                r.uniEnt = rs.getString(7);
                r.cantDev = rs.getBigDecimal(8);
                r.uniDev = rs.getString(9);
                r.placas = rs.getString(10);
                if (r.cantEnt == null) r.cantEnt = BigDecimal.ZERO;
                if (r.cantDev == null) r.cantDev = BigDecimal.ZERO;
                String estadoLimpio = (r.estado == null) ? "" : r.estado.trim();

                    // 2. Si es RECHAZADO o CERRADO (ignorando mayúsculas), el pendiente es 0
                    if (estadoLimpio.equalsIgnoreCase("RECHAZADA") || estadoLimpio.equalsIgnoreCase("CERRADA")) {
                        r.pendiente = BigDecimal.ZERO;
                    } else {
                        // 3. Si no, calculamos la resta normal
                        r.pendiente = r.cantEnt.subtract(r.cantDev);
                        // Opcional: Si la resta da negativo, dejarlo en 0
                        if (r.pendiente.compareTo(BigDecimal.ZERO) < 0) {
                            r.pendiente = BigDecimal.ZERO;
                        }
                    }
                out.add(r);
            }
            return out;
        }
    }
/*-----------------------------------------------
    Devoluciones de Aceites y Anticongelantes
 -----------------------------------------------*/
    private void onDevolver(){
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Selecciona un ticket."); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);
        HistorialRow sel = model.getAt(modelRow);
        String estadoActual = sel.estado != null ? sel.estado.toUpperCase() : "";
        if (estadoActual.equals("CERRADA") || estadoActual.equals("RECHAZADA")) {
            JOptionPane.showMessageDialog(this, "No se pueden realizar devoluciones en un ticket con estado: " + sel.estado);
            return;
        }
        if (sel.pendiente.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Este ticket no tiene pendiente por devolver.");
            return;
        }
        String cant = JOptionPane.showInputDialog(this, "Cantidad a devolver (pendiente: "+sel.pendiente+"):");
        if (cant == null) return;
        BigDecimal cantidad;
        try { cantidad = new BigDecimal(cant.trim()); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida."); return; }
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "Debe ser mayor a 0."); return; }
        if (cantidad.compareTo(sel.pendiente) > 0) { JOptionPane.showMessageDialog(this, "No puedes devolver más de lo pendiente."); return; }
        String unidad = sel.uniEnt; // forzamos misma unidad por consistencia
        try {
            service.registrarDevolucion(sel.id, cantidad, unidad, null);
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar devolución: "+ex.getMessage());
        }
    }

    private void validarBotonDevolucion() {
        int viewRow = table.getSelectedRow();
        
        // 1. Si no hay nada seleccionado, desactivar
        if (viewRow < 0) {
            btnDevolver.setEnabled(false);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        HistorialRow row = model.getAt(modelRow);

        // 3. Verificar condiciones
        boolean estadoInvalido = "CERRADA".equalsIgnoreCase(row.estado) 
                              || "RECHAZADA".equalsIgnoreCase(row.estado);
        
        boolean sinPendiente = row.pendiente.compareTo(BigDecimal.ZERO) <= 0;

        // 4. Si el estado es inválido O no hay nada pendiente, desactivar.
        if (estadoInvalido || sinPendiente) {
            btnDevolver.setEnabled(false);
        } else {
            btnDevolver.setEnabled(true);
        }
    }

/*----------------------
    EXportar a excel
 -----------------------*/
private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar historial (CSV)");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) {
                f = new java.io.File(f.getParentFile(), f.getName() + ".csv");
            }
            try (FileWriter w = new FileWriter(f)) {
                // Escribir encabezados
                for (int c=0; c<model.getColumnCount(); c++) {
                    if (c>0) w.write(",");
                    w.write(model.getColumnName(c));
                }
                w.write("\n");

                // --- 1. Definir el formato de fecha con hora y minutos ---
                java.text.SimpleDateFormat sdfExport = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

                // Escribir datos
                for (int r=0; r<model.getRowCount(); r++) {
                    for (int c=0; c<model.getColumnCount(); c++) {
                        if (c>0) w.write(",");
                        Object val = model.getValueAt(r,c);
                        
                        // --- 2. Lógica modificada para formatear fechas ---
                        String texto = "";
                        if (val != null) {
                            if (val instanceof java.util.Date) {
                                // Si es fecha, usamos el formato dd/MM/yyyy HH:mm
                                texto = sdfExport.format((java.util.Date) val);
                            } else {
                                // Si es otro dato, lo convertimos a texto normal
                                texto = String.valueOf(val);
                                // Opcional: Reemplazar comas por puntos para no romper el CSV
                                texto = texto.replace(",", "."); 
                            }
                        }
                        w.write(texto);
                        // -------------------------------------------------
                    }
                    w.write("\n");
                }
                w.flush();
                JOptionPane.showMessageDialog(this, "Archivo exportado: " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void aplicarFiltroAceites() {
        String q = (txtBuscar != null) ? txtBuscar.getText() : null;
        javax.swing.table.TableRowSorter<?> sorterLocal =
            (javax.swing.table.TableRowSorter<?>) table.getRowSorter();

        if (q == null || q.trim().isEmpty()) { sorterLocal.setRowFilter(null); return; }

        String[] tokens = q.trim().split("\\s+");
        java.util.List<javax.swing.RowFilter<Object,Object>> ands = new java.util.ArrayList<>();
        for (String t : tokens) {
            String pat = "(?i)" + java.util.regex.Pattern.quote(t);
            ands.add(regexEnTodasLasColumnas(table, pat));
        }
        sorterLocal.setRowFilter(javax.swing.RowFilter.andFilter(ands));
    }

    private javax.swing.RowFilter<Object,Object> regexEnTodasLasColumnas(javax.swing.JTable table, String regex) {
        final int cols = table.getColumnCount();
        final int[] idx = new int[cols];
        for (int i = 0; i < cols; i++) idx[i] = i;
        return javax.swing.RowFilter.regexFilter(regex, idx);
    }

    // ===== Modelos/DTO =====
    private static class HistorialRow {
        Integer id; Date fecha; String estado; String solicitante; String fluido; BigDecimal cantEnt; String uniEnt; BigDecimal cantDev; String uniDev; String placas; BigDecimal pendiente;
    }

    private static class HistorialModel extends AbstractTableModel {
        java.util.List<HistorialRow> rows = new java.util.ArrayList<HistorialRow>();
        public void setData(java.util.List<HistorialRow> data){ rows.clear(); if (data!=null) rows.addAll(data); fireTableDataChanged(); }
        public int getRowCount(){ return rows.size(); }
        public int getColumnCount(){ return 11; }
        public String getColumnName(int c){ return new String[]{"ID","Fecha","Estado","Solicitante","Fluido","Cant. Ent.","Unidad Ent.","Cant. Dev.","Unidad Dev.","Placas","Pendiente"}[c]; }
        public Class<?> getColumnClass(int c){ return new Class[]{Integer.class, java.util.Date.class, String.class, String.class, String.class, java.math.BigDecimal.class, String.class, java.math.BigDecimal.class, String.class, String.class, java.math.BigDecimal.class}[c]; }
        public Object getValueAt(int r, int c){
            HistorialRow x = rows.get(r);
            switch(c){
                case 0:return x.id; case 1:return x.fecha; case 2:return x.estado; case 3:return x.solicitante; case 4:return x.fluido; case 5:return x.cantEnt; case 6:return x.uniEnt; case 7:return x.cantDev; case 8:return x.uniDev; case 9:return x.placas; case 10:return x.pendiente; default:return null;
            }
        }
        public HistorialRow getAt(int idx){ return rows.get(idx); }
    }
}