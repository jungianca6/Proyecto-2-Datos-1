import org.opencv.core.Core;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import java.io.File;


class VentanaCliente extends JFrame implements Runnable{
    private JPanel panelCliente;
    private JLabel Cliente;
    private JTextField operacionA,resultadoA,operacionL,resultadoL;
    private JButton solucionA,solucionL;

    /**
     * constructor de la ventana
     */

    public VentanaCliente() {
        this.setBounds(600, 200, 450, 450);
        setTitle("Cliente");

        ComponentesCliente();

        /**
         * construimos el hilo para que siempre escuche
         */
        Thread mihilo = new Thread(this);
        mihilo.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void ComponentesCliente(){
        panelCliente();
        etiquetaCliente();
        colocarCajadeTexto();
        colocarBoton();
    }

    private void panelCliente(){
        panelCliente = new JPanel();
        panelCliente.setLayout(null);
        this.getContentPane().add(panelCliente);
    }

    private void etiquetaCliente(){
        Cliente = new JLabel("Cliente",SwingConstants.CENTER);
        panelCliente.add(Cliente);
        Cliente.setBounds(140,20,100,25);
        Cliente.setForeground(Color.WHITE);
        Cliente.setBackground(Color.BLACK);
        Cliente.setFont(new Font("times new roman", Font.PLAIN,20));
        Cliente.setOpaque(true);
    }

    private void colocarCajadeTexto(){
        operacionA = new JTextField();
        operacionA.setBounds(60,325,150,20);
        panelCliente.add(operacionA);

        resultadoA = new JTextField();
        resultadoA.setBounds(60,285,150,20);
        panelCliente.add(resultadoA);

        operacionL = new JTextField();
        operacionL.setBounds(250,325,150,20);
        panelCliente.add(operacionL);

        resultadoL = new JTextField();
        resultadoL.setBounds(250,285,150,20);
        panelCliente.add(resultadoL);
    }

    private void colocarBoton() {
        solucionA = new JButton("Solución");
        solucionA.setBounds(90, 370, 100, 30);
        panelCliente.add(solucionA);
        solucionA.setEnabled(true);

        solucionL = new JButton("Solución");
        solucionL.setBounds(290, 370, 100, 30);
        panelCliente.add(solucionL);
        solucionL.setEnabled(true);


        ActionListener enviaOperacionA = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    /**
                     * creacion del socket
                     */
                    Socket misocket = new Socket("localhost",9090);

                    paqueteDatos operaciones = new paqueteDatos();
                    operaciones.setAritmetica(operacionA.getText());

                    ObjectOutputStream salidaOperacion = new ObjectOutputStream(misocket.getOutputStream());
                    salidaOperacion.writeObject(operaciones);

                    misocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        solucionA.addActionListener(enviaOperacionA);

    }

    @Override
    public void run() {
        int port;
        port = 9091;
        for (int i = port; i<9100;i++){
            try {
                ServerSocket servidorcliente = new ServerSocket(i);
                Socket cliente;
                paqueteDatos packRecibido;

                while(true){
                    cliente = servidorcliente.accept();
                    ObjectInputStream solucionRecibida = new ObjectInputStream(cliente.getInputStream());

                    packRecibido = (paqueteDatos) solucionRecibida.readObject();;
                    resultadoA.setText(packRecibido.getAritmetica());
                }
            } catch (IOException | ClassNotFoundException e) {
                //throw new RuntimeException(e);
            }
        }
    }
}

class paqueteDatos implements Serializable{
    private String aritmetica;

    public String getAritmetica() {
        return aritmetica;
    }

    public void setAritmetica(String aritmetica) {
        this.aritmetica = aritmetica;
    }
}

class paqueteLogica implements Serializable{
    private String logica;

    public String getLogica() {
        return logica;
    }

    public void setLogica(String logica) {
        this.logica = logica;
    }
}

public class Cliente {
    public static void main(String[] args) {
        VentanaCliente cliente = new VentanaCliente();
        cliente.setVisible(true);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
