import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FrameCliente extends JFrame implements Runnable{ //implementa interface Runnable para poder dejar este cliente a la escucha permanente

    public FrameCliente(){
        setBounds(250,100,300,350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat-Pulenta");
        setLayout(new FlowLayout());

        //-------------------------------------------------------------------
        Thread thread = new Thread(this);
        thread.start();
        //-------------------------------------------------------------------

        String nickIngresado = JOptionPane.showInputDialog("Nick: ");

        ipTxt = new JLabel("     IP:");
        textField = new JTextField(17);
        enviar = new JButton("Enviar");
        jTextAreaCliente = new JTextArea(14,24);

        jScrollPane = new JScrollPane(jTextAreaCliente);
        nombre = new JLabel("Nick: ");
        nick = new JLabel(nickIngresado);
        ip = new JTextField(8);

        //-------------------------------------------------------------------
        enviar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                jTextAreaCliente.append("\n Yo: " + textField.getText());
                try
                {
                    Socket miSocket = new Socket("10.168.0.103",9999); //se abre una conección

                    //------Con esta instancia cargamos con los datos escritos en componentes:
                    PaqueteEnvio datos = new PaqueteEnvio();
                    datos.setNick(nick.getText());
                    datos.setMensaje(textField.getText());
                    datos.setIp(ip.getText());

                    ObjectOutputStream enviarObjeto = new ObjectOutputStream(miSocket.getOutputStream()); //crea el flujo de datos

                    enviarObjeto.writeObject(datos); //Carga este flujo de datos con el objeto

                    miSocket.close();
                    enviarObjeto.close();

                } catch (IOException ex) {
                    System.out.println("No se ha podido conectar");
                }finally {
                    textField.setText(""); //vuelve a poner el textfield del chat en blanco
                }

            }
        });

        //---------------------------Agrega Componentes---------------------------------
        add(nombre);
        add(nick);
        add(ipTxt);
        add(ip);
        add(jScrollPane);
        add(textField);
        add(enviar);

        //------------------------------------------------------------------------------
        setVisible(true);
    }

    @Override //---------------sobre escritura del metodo de la interface Runnable para poner a la escucha a un hilo de ejecución----------
    public void run() {

        try {
            ServerSocket servidor_cliente= new ServerSocket(9090); //para indicarle que debe estar a la escucha en el puerto 9090

            Socket cliente;
            PaqueteEnvio paqueteRecibido;

            while(true){
                cliente = servidor_cliente.accept(); //acepta todas las conecciones del exterior

                //-------crea un flojo de entrada capaz de recibir objetos---------------
                ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());

                paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();

               jTextAreaCliente.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private JLabel ipTxt,nombre,nick;
    private JTextField textField, ip;
    private JButton enviar;
    private JTextArea jTextAreaCliente;
    private JScrollPane jScrollPane;


}
