package Principal;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;

public class consulta extends javax.swing.JFrame {

    Inicio vw = new Inicio();
    Conexion ccn = new Conexion();
    String polizanum = null;
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 5) {
                return true;
            } else {
                return false;
            }
        }
    };
    public consulta() {
        initComponents();
        vw.dispose();
        cargarimg();
        this.setSize(800, 400);
        this.setLocationRelativeTo(null);
        this.setTitle("Polizas sin CFDI");

        //Se cargara el nombre de la BD seleccionada al Jlabel
        NameBD.setText(vw.BDselect);
        MostrarTabla();
        //
        CargarJC();
        JCpolizas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String poliza = (String) JCpolizas.getSelectedItem();
                if (poliza.equals("Ingresos")) {
                    polizanum = "1";
                } else if (poliza.equals("Egresos")) {
                    polizanum = "2";
                } else if (poliza.equals("Diario")) {
                    polizanum = "3";
                }

            }
        });

    }
    
     public void cargarimg(){
        //Cargar img de calendario inicio
        String urlfechaini = "/images/Calendar_48px.png";
        ImageIcon im_fechaini = new ImageIcon(this.getClass().getResource(urlfechaini));
        ImageIcon imgfecha_ini = new ImageIcon (im_fechaini.getImage().getScaledInstance(imgfechaini.getWidth(), imgfechaini.getHeight(), Image.SCALE_DEFAULT));
        imgfechaini.setIcon(imgfecha_ini);
        //cargar img de calendario fin
        String urlfechafin = "/images/Calendar_48px.png";
        ImageIcon im_fechafin = new ImageIcon(this.getClass().getResource(urlfechafin));
        ImageIcon imgfecha_fin = new ImageIcon (im_fechafin.getImage().getScaledInstance(imgfechafin.getWidth(), imgfechafin.getHeight(), Image.SCALE_DEFAULT));
        imgfechafin.setIcon(imgfecha_fin);
        
        //Cargar img de poliza
        String urlpoliza = "/images/ReportCard_48px.png";
        ImageIcon im_poliza = new ImageIcon(this.getClass().getResource(urlpoliza));
        ImageIcon imgpoli = new ImageIcon (im_poliza.getImage().getScaledInstance(imgpoliza.getWidth(), imgpoliza.getHeight(), Image.SCALE_DEFAULT));
        imgpoliza.setIcon(imgpoli);
    }

    public void CargarJC() {
        /*Se carga el JC para el mostrar el nombre de las polizas*/
        JCpolizas.removeAllItems();
        JCpolizas.addItem("Seleccion un Tipo de poliza");
        JCpolizas.addItem("Ingresos");
        JCpolizas.addItem("Egresos");
        JCpolizas.addItem("Diario");
    }

    public void MostrarTabla() {
        /*Se crea el metodo de modelo de tabla se le mandan todas las columnas debido a la consulta
        Y para despues esto mandarselo a la tabla del Jframe*/
        modelo.addColumn("Folio");
        modelo.addColumn("TipoPoliza");
        modelo.addColumn("Fecha");
        modelo.addColumn("Concepto");
        modelo.addColumn("NombreUsuario");
        JTpolizas.setModel(modelo);

        /*Se le mandan los diferentes anchos de las columnas para que tengan una
        buena visibilidad para el usuario final*/
        JTpolizas.getColumnModel().getColumn(0).setPreferredWidth(30);
        JTpolizas.getColumnModel().getColumn(1).setPreferredWidth(30);
        JTpolizas.getColumnModel().getColumn(2).setPreferredWidth(100);
        JTpolizas.getColumnModel().getColumn(3).setPreferredWidth(300);
        JTpolizas.getColumnModel().getColumn(4).setPreferredWidth(100);

    }

    public void ConsultarPolizas() {
        //Se hace la conversion de las fechas para poder realizar la consulta al SQLSERVER
        int fechaini = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(JCinicio.getDate()));
        int fechafin = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(JCfin.getDate()));
        //System.out.println("Fecha inicio: " + fechaini + " Fecha fin: " + fechafin + " Tipo Poliza: " + polizanum);

        if (JCinicio == null && JCfin == null && polizanum == null) {
            JOptionPane.showMessageDialog(null, "Error en dejar campos vacios", "Error en campos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Connection cca = ccn.ConectarBD(vw.BDselect);
            String Consulta = "SELECT p.Folio, t.Nombre AS TipoPoliza, p.Fecha, p.Concepto, u.Nombre AS NombreUsuario \n"
                    + " FROM Polizas p \n"
                    + " LEFT JOIN AsocCFDIs a ON p.Guid = a.GuidRef \n"
                    + " INNER JOIN GeneralesSQL.dbo.Usuarios u ON u.Id = p.IdUsuario \n"
                    + " INNER JOIN TiposPolizas t ON t.Id = p.TipoPol\n"
                    + " WHERE a.GuidRef IS NULL \n"
                    + " AND p.TipoPol = " + polizanum + "\n"
                    + " AND p.Fecha BETWEEN '" + fechaini + "' AND '" + fechafin + "'\n"
                    + " AND NOT p.Concepto LIKE 'CTO%' /*COSTO CUENTAS - DIARIO*/\n"
                    + " AND NOT p.Concepto LIKE 'TRASPA%' /*TRASPASOS - DIARIO */\n"
                    + " AND NOT p.Concepto LIKE 'FLUC%' /*FLUCTUACIONES*/\n"
                    + " AND NOT p.Concepto LIKE 'CANCELACION%' /* */\n"
                    + " AND NOT p.Concepto LIKE 'EDIFI%' /*EDIFICACION Y OPERACIOn */\n"
                    + " AND NOT p.Concepto LIKE 'AJUSTE%' /* AJUSTE OCMAI*/\n"
                    + " AND NOT p.Concepto LIKE 'ANTICIPOS%' /* */\n"
                    + " AND NOT p.Concepto LIKE 'DETERMINACION%' /* */\n"
                    + " AND NOT p.Concepto LIKE '%ISR%' /* */\n"
                    + " AND NOT p.Concepto LIKE 'REGISTRO%' /* */\n"
                    + " AND NOT p.Concepto LIKE 'DEVOLUCION%' /* */\n"
                    + " AND NOT p.Concepto LIKE 'BODEGA%' /*BODEGA INGRESOS */\n"
                    + " AND NOT p.Concepto LIKE 'REEMBOLSO%' /*REEMBOLSOS DE INGRESOS */\n"
                    + " AND NOT p.Concepto LIKE 'DISPOSICION%' /* INGRESOS*/\n"
                    + " AND NOT p.Concepto LIKE 'ABONO%' /* EGRESOS*/\n"
                    + " AND NOT p.Concepto LIKE 'PROVISINAL%' /* PROVISIONAL - EGRESOS*/\n"
                    + " AND NOT p.Concepto LIKE 'PROVISION%DIF%' /* PROVISION DIF - DIARIO*/\n"
                    + " AND NOT p.Concepto LIKE 'PG%IM%' /*NOMINAS PG IMSS*/\n"
                    + " AND NOT p.Concepto LIKE 'PG%FI%' /*NOMINAS PAGO FINITQUITO*/\n"
                    + " AND NOT p.Concepto LIKE 'PAGO%PROVI%' /* PAGO PROVISIONAL */\n"
                    + " AND NOT p.Concepto LIKE 'REGISTRO%' /*REGISTRO DE DEPREACIONES - DIARIO */\n"
                    + " ORDER BY Fecha ASC";
            try {
                Statement st = cca.createStatement();
                ResultSet rs = st.executeQuery(Consulta);
                String datos[] = new String[5];
                while (rs.next()) {
                    datos[0] = rs.getString(1);
                    datos[1] = rs.getString(2);
                    datos[2] = rs.getString(3);
                    datos[3] = rs.getString(4);
                    datos[4] = rs.getString(5);
                    modelo.addRow(datos);
                }
                JTpolizas.setModel(modelo);
                cca.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error en crear la consulta: " + e, "Error en la consulta", JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        NameBD = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        back = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        exportar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        JCfin = new com.toedter.calendar.JDateChooser();
        JCinicio = new com.toedter.calendar.JDateChooser();
        perro = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTpolizas = new javax.swing.JTable();
        Con = new javax.swing.JButton();
        NVCon = new javax.swing.JButton();
        JCpolizas = new javax.swing.JComboBox<>();
        imgpoliza = new javax.swing.JLabel();
        imgfechaini = new javax.swing.JLabel();
        imgfechafin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(924, 644));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 620));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 620));

        jPanel2.setBackground(new java.awt.Color(153, 0, 0));
        jPanel2.setMinimumSize(new java.awt.Dimension(900, 620));
        jPanel2.setPreferredSize(new java.awt.Dimension(900, 620));
        jPanel2.setLayout(null);

        NameBD.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        NameBD.setText("NameBD");
        jPanel2.add(NameBD);
        NameBD.setBounds(470, 10, 220, 31);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setText("Nombre de la base de datos a consultar:");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(90, 10, 360, 31);

        back.setText("Regresar");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel2.add(back);
        back.setBounds(30, 570, 90, 25);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fin de la fecha");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(210, 70, 120, 30);

        exportar.setText("Exportar a .XLS");
        exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportarActionPerformed(evt);
            }
        });
        jPanel2.add(exportar);
        exportar.setBounds(180, 570, 120, 25);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Inicio de la fecha");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(10, 70, 120, 30);
        jPanel2.add(JCfin);
        JCfin.setBounds(210, 100, 160, 30);
        jPanel2.add(JCinicio);
        JCinicio.setBounds(10, 100, 160, 30);

        perro.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        perro.setText("Ingresar el tipo de poliza");
        jPanel2.add(perro);
        perro.setBounds(420, 70, 160, 30);

        JTpolizas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(JTpolizas);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(10, 200, 870, 350);

        Con.setText("Consultar");
        Con.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConActionPerformed(evt);
            }
        });
        jPanel2.add(Con);
        Con.setBounds(330, 570, 130, 25);

        NVCon.setText("Nueva Consulta");
        NVCon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NVConActionPerformed(evt);
            }
        });
        jPanel2.add(NVCon);
        NVCon.setBounds(490, 570, 130, 25);

        JCpolizas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(JCpolizas);
        JCpolizas.setBounds(400, 100, 220, 30);
        jPanel2.add(imgpoliza);
        imgpoliza.setBounds(550, 50, 40, 40);
        jPanel2.add(imgfechaini);
        imgfechaini.setBounds(120, 50, 40, 40);
        jPanel2.add(imgfechafin);
        imgfechafin.setBounds(310, 50, 40, 40);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        // Boton para regresar al inicio del programa para inicializar otra base de datos
        vw.BDselect = null;
        vw.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_backActionPerformed

    private void exportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportarActionPerformed
        /*Si la tabla tiene cero filas noextrae nada.*/
        if (JTpolizas.getRowCount() > 0) {
            //Variable para sacar el File
            JFileChooser chooser = new JFileChooser();
            //Se le manda el nombre del archivo junto con la extension.
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Guardar archivo");
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                List tb = new ArrayList();
                List nom = new ArrayList();
                tb.add(JTpolizas);
                //Se le agrega el nombre a la hoja de calculo.
                nom.add("Reporte de polizas sin CFDI");
                // al archivo se le concatena la extension .xls
                String file = chooser.getSelectedFile().toString().concat(".xls");
                try {
                    //Se manda a la clase creada para exportar el documento,
                    Export e = new Export(new File(file), tb, nom);
                    if (e.export()) {
                        JOptionPane.showMessageDialog(null, "Los datos fueron exportados a excel en el directorio seleccionado", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Hubo un error " + e.getMessage(), " Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Mensaje de error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_exportarActionPerformed

    private void ConActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConActionPerformed

        ConsultarPolizas();
    }//GEN-LAST:event_ConActionPerformed

    private void NVConActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NVConActionPerformed
        modelo.setRowCount(0);

    }//GEN-LAST:event_NVConActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new consulta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Con;
    private com.toedter.calendar.JDateChooser JCfin;
    private com.toedter.calendar.JDateChooser JCinicio;
    private javax.swing.JComboBox<String> JCpolizas;
    private javax.swing.JTable JTpolizas;
    private javax.swing.JButton NVCon;
    private javax.swing.JLabel NameBD;
    private javax.swing.JButton back;
    private javax.swing.JButton exportar;
    private javax.swing.JLabel imgfechafin;
    private javax.swing.JLabel imgfechaini;
    private javax.swing.JLabel imgpoliza;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel perro;
    // End of variables declaration//GEN-END:variables
}
