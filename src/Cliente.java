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
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * Esta clase crea la ventana de la interfaz gráfica del servidor
 */
class VentanaCliente extends JFrame implements Runnable{

    /**
     * El panel de la interfaz que permite colocar los otros componentes
     */
    private JPanel panelCliente;
    /**
     * La etiqueta de titulo del servidor
     */
    private JLabel Cliente;
    /**
     * Los campos de texto de la interfaz del cliente
     */
    private JTextField operacionA,resultadoA;
    /**
     * Botones de la interfaz
     */
    private JButton solucionA,OCR;

    /**
     * Constructor de la ventana
     */
    public VentanaCliente() {
        this.setBounds(600, 200, 450, 450);
        setTitle("Cliente");

        ComponentesCliente();

        /*
         * construimos el hilo para que siempre escuche
         */
        Thread mihilo = new Thread(this);
        mihilo.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Añade los componentes a la interfaz
     */
    private void ComponentesCliente(){
        panelCliente();
        etiquetaCliente();
        colocarCajadeTexto();
        colocarBoton();
    }

    /**
     * Crea el panel de la interfaz del cliente
     */
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

    /**
     * Crea las cajas de texto de la interfaz del cliente
     */
    private void colocarCajadeTexto(){
        operacionA = new JTextField();
        operacionA.setBounds(60,325,150,20);
        panelCliente.add(operacionA);

        resultadoA = new JTextField();
        resultadoA.setBounds(60,285,150,20);
        panelCliente.add(resultadoA);
    }

    /**
     * Crea el botón de la interfaz del cliente
     */
    private void colocarBoton() {
        solucionA = new JButton("Solución");
        solucionA.setBounds(90, 370, 100, 30);
        OCR = new JButton("OCR");
        OCR.setBounds(150,370,100,30);
        panelCliente.add(solucionA);
        solucionA.setEnabled(true);

        ActionListener enviaOperacionA = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    /**
                     * creacion del socket
                     */
                    Socket misocket = new Socket("localhost",9090);

                    paqueteDatos operaciones = new paqueteDatos();
                    operaciones.setOperacion(operacionA.getText());

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

    private void realizarOCR() {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath("C:\\Users\\jungi\\Desktop\\Tesseract\\Tess4J-3.4.8-src\\Tess4J\\tessdata");
            String text = tesseract.doOCR(new File("test.png"));
            resultadoA.setText(text);
        } catch (TesseractException e) {
            e.printStackTrace();
            resultadoA.setText("Error en el OCR");
        }
    }

    /**
     * Método que permite la comunicacion y envío de datos
     * entre servidor y cliente
     */
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
                    resultadoA.setText(packRecibido.getResultado());
                }
            } catch (IOException | ClassNotFoundException e) {
                //throw new RuntimeException(e);
            }
        }
    }
}


/**
 * Esta clase serializa los datos en bits,
 * el cual permite que puedan ser enviados por medio de los sockets
 */

class paqueteDatos implements Serializable{
    private String operacion,resultado;

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String aritmetica) {
        this.operacion = aritmetica;
    }
}




public class Cliente {
    public static void main(String[] args) {
        VentanaCliente cliente = new VentanaCliente();
        cliente.setVisible(true);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
