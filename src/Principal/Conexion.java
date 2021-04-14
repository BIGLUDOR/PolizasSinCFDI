package Principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import javax.swing.JOptionPane;

public class Conexion {

    Connection cc = null;
    Connection cca = null;
    String password;
    String instancia;
    String ip;
    String user;
    String puerto;

    //se hace la consulta de los accesos por constructor.
    public Conexion() {
        BufferedReader br = null;
        File accesos = null;
        FileReader fr = null;

        try {
            accesos = new File("C:\\PSCFDI\\accesos.txt");
            fr = new FileReader(accesos);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("instancia=")) {
                    instancia = linea.substring(10);
                } else if (linea.startsWith("user=")) {
                    user = linea.substring(5);
                } else if (linea.startsWith("pass=")) {
                    password = linea.substring(5);
                } else if (linea.startsWith("ip=")) {
                    ip = linea.substring(3);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Error en leer el documento: "+e,"Error de documento", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Error al cerrar el documento de accesos: "+ex,"Error en el documento", JOptionPane.ERROR);
            }
        }

    }

    public Connection conectar() {
        //URL para Conexion al servidor
        String URL = "jdbc:sqlserver://" + ip + "\\" + instancia + ":1433";
        try {
            //Driver para hacer la conexion de la base de datos
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cc= DriverManager.getConnection(URL, user, password);
            /*Agregamos las excepciones
            1.- No se encontro el driver
            2.- Excepciion al no conectarse a la base de datos (autenticarse)
            3.-Excepcion general de toda la conexion
             */
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se encontro el driver, revisar el driver para listar las Base de datos " + e, "Error en el Driver", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en la conexion para listar las Base de datos " + ex, "Error en conexion a BD", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ec) {
            JOptionPane.showMessageDialog(null, "Error en el codigo de la conexion" + ec, "Error en codigo", JOptionPane.INFORMATION_MESSAGE);
        }
        //}
        return cc;
    }

    //Esta segunda conexion se hace para poder conectarse a la base de datos seleccionada.
    public Connection ConectarBD(String NameBD) {
        String URL = "jdbc:sqlserver://" + ip + "\\" + instancia + ":1433;databaseName=" + NameBD;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cca = DriverManager.getConnection(URL, user, password);
            //JOptionPane.showConfirmDialog(null, "Conexión a la base de datos: " + NameBD, "Conexion Exitosa", JOptionPane.DEFAULT_OPTION);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se encontro el driver, revisar el driver para listar las Base de datos " + e, "Error en el Driver", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en la conexion para listar las Base de datos " + ex, "Error en conexion a BD", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ec) {
            JOptionPane.showMessageDialog(null, "Error en el codigo de la conexion" + ec, "Error en codigo", JOptionPane.INFORMATION_MESSAGE);
        }
        return cca;
    }

    Statement createStatement() {
        throw new UnsupportedOperationException("No soportado");
    }

}
