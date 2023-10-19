import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

class VentanaServer extends JFrame implements Runnable{
    private JPanel panelServidor;
    private JLabel Servidor;
    private JButton solucionS;
    private JTextField operacion;
    private JTextField resultado;

    /**
     * constructor de la ventana
     */

    public VentanaServer() {
        this.setBounds(500, 200, 450, 450);
        setTitle("Servidor");

        componentesServer();

        /**
         * construimos el hilo para que siempre escuche
         */
        Thread mihilo = new Thread(this);
        mihilo.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void componentesServer(){
        panelServidor();
        etiquetaServer();
        colocarCajadeTexto();
        colocarBoton();
    }

    private void panelServidor(){
        panelServidor = new JPanel();
        panelServidor.setLayout(null);
        this.getContentPane().add(panelServidor);
    }

    private void etiquetaServer(){
        Servidor = new JLabel("Servidor",SwingConstants.CENTER);
        panelServidor.add(Servidor);
        Servidor.setBounds(50,15,100,25);
        Servidor.setForeground(Color.WHITE);
        Servidor.setBackground(Color.BLACK);
        Servidor.setFont(new Font("times new roman", Font.PLAIN,20));
        Servidor.setOpaque(true);
    }

    private void colocarCajadeTexto(){
        operacion = new JTextField();
        operacion.setBounds(60,325,250,20);
        panelServidor.add(operacion);

        resultado = new JTextField();
        resultado.setBounds(60,205,250,20);
        panelServidor.add(resultado);
    }

    private void colocarBoton() {
        solucionS = new JButton("Solución");
        solucionS.setBounds(140, 370, 100, 30);
        panelServidor.add(solucionS);
        solucionS.setEnabled(true);

        ActionListener calcular = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cadena = operacion.getText();
                ArbolBinarioExp ABE = new ArbolBinarioExp(cadena);
                resultado.setText(""+ABE.evaluaExpresion());
            }
        };
        solucionS.addActionListener(calcular);
    }

    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(9090);

            while(true) {
                /**
                 *Permite que acepte las conexiones del exterior
                 */
                Socket misocket = servidor.accept();
                /**cierra el flujo de datos*/
                misocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class NodeArbol {
    protected Object data;
    protected NodeArbol left;
    protected NodeArbol right;

    public NodeArbol(Object data) {
        this.data = data;
        left = right = null;
    }
}
class TraversalTree {
    NodeArbol raiz;
    public TraversalTree(){
        raiz = null;
    }
    public void InOrder(NodeArbol node){
        if (node != null){
            InOrder(node.left);
            System.out.print(node.data + " ");
            InOrder(node.right);
        }
    }
}

class NodePila{
    protected NodeArbol data;
    protected NodePila next;

    public NodePila(NodeArbol x){
        this.data = x;
        next = null;
    }
}

class PilaArbolExp{
    private NodePila tope;

    public PilaArbolExp(){
        tope = null;
    }

    public void insertar (NodeArbol elemento){
        NodePila nuevo;
        nuevo = new NodePila(elemento);
        nuevo.next = tope;
        tope = nuevo;
    }

    public boolean pilaVacia(){
        return tope == null;
    }

    public NodeArbol topePila(){
        return tope.data;
    }
    public void ReiniciarPila(){
        tope = null;
    }
    public NodeArbol quitar(){
        NodeArbol aux = null;
        if (!pilaVacia()){
            aux = tope.data;
            tope = tope.next;
        }
        return aux;
    }
}

class ArbolBinarioExp{
    NodeArbol raiz;

    public ArbolBinarioExp(){
        raiz= null;
    }

    /**
     * crea el arbol de expresiones a partir de la cadena
     */
    public ArbolBinarioExp(String cadena){
        raiz = creaArbolBE(cadena);
    }

    public void reiniciaArbol(){
        raiz = null;
    }

    public void creaNodo(Object data){
        raiz = new NodeArbol (data);
    }
    public NodeArbol creaSubArbol(NodeArbol dato2,NodeArbol dato1, NodeArbol operador){
        operador.left= dato1;
        operador.right= dato2;
        return operador;
    }

    public boolean arbolVacio(){
        return raiz == null;
    }

    private String inOrden(NodeArbol subArbol,String c){
        String cadena;
        cadena = "";
        if (subArbol != null){
            cadena = c + inOrden(subArbol.left,c) + subArbol.data.toString() +"\n"+
                    inOrden(subArbol.right,c);
        }
        return cadena;
    }

    public String toString(){
        String cadena = "";
        cadena = inOrden(raiz,cadena);
        return cadena;
    }

    private int prioridad(char c){
        int p=100;
        switch (c){
            case '^':
                p=30;
                break;
            case '*':
            case '/':
                p=20;
                break;
            case '+':
            case '-':
            case '&':
            case '|':
            case '~':
            case '#':
                p=10;
                break;
            default:
                p=0;
        }
        return p;
    }

    private boolean esOperador(char c){
        boolean resultado;
        switch(c){
            case '(':
            case ')':
            case '^':
            case '*':
            case '/':
            case '+':
            case '-':
            case '&':
            case '|':
            case '~':
            case '#':
                resultado = true;
                break;
            default:
                resultado = false;
        }
        return resultado;
    }

    private NodeArbol creaArbolBE (String cadena){
        PilaArbolExp pilaOperadores;
        PilaArbolExp pilaExpresiones;
        NodeArbol token;
        NodeArbol op1;
        NodeArbol op2;
        NodeArbol op;

        pilaOperadores = new PilaArbolExp();
        pilaExpresiones = new PilaArbolExp();
        char caracterEvaluado;
        for (int i=0; i<cadena.length();i++){
            caracterEvaluado = cadena.charAt(i);
            token = new NodeArbol(caracterEvaluado);
            if (Character.isDigit(caracterEvaluado)){
                StringBuilder numero = new StringBuilder();
                while (i<cadena.length() && Character.isDigit(cadena.charAt(i))){
                    numero.append(cadena.charAt(i));
                    i++;
                }
                i--;
                NodeArbol numeroNodo = new NodeArbol(numero);
                pilaExpresiones.insertar(numeroNodo);
            }
            else if (!esOperador(caracterEvaluado)){

            }else{ /**es un operador*/
                switch(caracterEvaluado){
                    case '(':
                        pilaOperadores.insertar(token);
                        break;
                    case ')':
                        while (!pilaOperadores.pilaVacia() &&
                                !pilaOperadores.topePila().data.equals('(')){
                            op2 = pilaExpresiones.quitar();
                            op1 = pilaExpresiones.quitar();
                            op = pilaOperadores.quitar();
                            op = creaSubArbol(op2,op1,op);
                            pilaExpresiones.insertar(op);
                        }
                        /**
                         * Lo quitamos de la pila de operadores, porque
                         * el parentesis no forma parte de nuestra expresión
                         */
                        pilaOperadores.quitar();
                        break;
                    default:
                        while(!pilaOperadores.pilaVacia() && prioridad(caracterEvaluado)
                                <= prioridad(pilaOperadores.topePila().data.toString().charAt(0))){
                            op2 = pilaExpresiones.quitar();
                            op1 = pilaExpresiones.quitar();
                            op = pilaOperadores.quitar();
                            op = creaSubArbol(op2,op1,op);
                            pilaExpresiones.insertar(op);
                        }
                        pilaOperadores.insertar(token);

                }
            }

        }
        /**
         * En el caso que la pila de operadores no este vacia
         */
        while (!pilaOperadores.pilaVacia()){
            op2 = pilaExpresiones.quitar();
            op1 = pilaExpresiones.quitar();
            op= pilaOperadores.quitar();
            op= creaSubArbol(op2,op1,op);
            pilaExpresiones.insertar(op);
        }
        /**
         * Al final se retorna el arbol completo de expresiones
         */
        op = pilaExpresiones.quitar();
        return op;
    }

    public double evaluaExpresion(){
        return evalua(raiz);
    }

    private double evalua(NodeArbol subarbol){
        double acum=0;
        if (!esOperador(subarbol.data.toString().charAt(0))){
            return Double.parseDouble(subarbol.data.toString());
        }else{
            switch(subarbol.data.toString().charAt(0)){
                case '^':
                    acum = acum + Math.pow(evalua(subarbol.left),evalua(subarbol.right));
                    break;
                case '*':
                    acum = acum + evalua(subarbol.left) * evalua(subarbol.right);
                    break;
                case '/':
                    acum = acum + evalua(subarbol.left) / evalua(subarbol.right);
                    break;
                case '+':
                    acum = acum + evalua(subarbol.left) + evalua(subarbol.right);
                    break;
                case '-':
                    acum = acum + evalua(subarbol.left) - evalua(subarbol.right);
                    break;
            }
        }
        return acum;
    }
}


public class Servidor {
    public static void main(String[] args) {
        System.out.println("hola");

        VentanaServer servidor = new VentanaServer();
        servidor.setVisible(true);

        TraversalTree arbol = new TraversalTree();
        arbol.raiz = new NodeArbol("+");
        arbol.raiz.left = new NodeArbol("*");
        arbol.raiz.right = new NodeArbol(7);
        arbol.raiz.left.left = new NodeArbol (6);
        arbol.raiz.left.right = new NodeArbol (5);
        arbol.InOrder(arbol.raiz);
    }
}
