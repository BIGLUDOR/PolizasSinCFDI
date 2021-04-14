/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JTable;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 *
 * @author SISTEMAS-LUIS
 */
public class Export {

    private File file;
    private List tabla;
    private List nom_files;

    public Export(File file, List tabla, List nom_files) throws Exception {
        //Se hace el constructor de la clase enviandole losparametros.
        this.file = file;
        this.tabla = tabla;
        this.nom_files = nom_files;
        if (nom_files.size() != tabla.size()) {
            throw new Exception("Error");
        }
    }

    public boolean export() {
        try {
            //La salida del archivo con el dataoutput, se le pasa el parametro File
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            //Se escribe el libre con la salida.
            WritableWorkbook w = Workbook.createWorkbook(out);
            //Se hace un recorrido de los titulos de las columas
            for (int index = 0; index < tabla.size(); index++) {
                //Tabla ya trae el parametro de la JTpolizas
                JTable table = (JTable) tabla.get(index);
                WritableSheet s = w.createSheet((String) nom_files.get(index), 0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        //Se recorren los titulos por columna y despues por fila.
                        Object titulo = table.getColumnName(i);
                        s.addCell(new Label(i, 0, String.valueOf(titulo)));
                        //En este caso el contador se pone i y 0 para que solo se quede con una fila
                    }
                }
                for (int i = 0; i < table.getColumnCount(); i++) {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        Object object = table.getValueAt(j, i);
                        s.addCell(new Label(i, j + 1, String.valueOf(object)));
                        //Se mandan las filas a escribir, en este caso el J se aumenta porque la fila 0 (columnas ya esta ocupada.)
                    }
                }
            }
            w.write(); // Escribimos archivo
            w.close(); //Cerramos
            return true; //Devolvemos verdadero para que la sentencia if se cumpla.
        } catch (Exception e) {
            return false;
        }
    }

}
