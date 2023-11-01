import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FrameServidor extends JFrame implements Runnable
{

    public FrameServidor()
    {
        setBounds(650, 100, 350, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("SERVIDOR...");

        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        Thread hilo = new Thread(this);
        hilo.start();
    }


    @Override
    public void run() {
        try
        {
            ServerSocket serverSocket = new ServerSocket(9999);

            String nick, ip, mensaje;
            PaqueteEnvio paqueteEntrante;

            while(true)
            {
                Socket miSocket = serverSocket.accept();

                ObjectInputStream datosEntrada = new ObjectInputStream(miSocket.getInputStream());

                paqueteEntrante = (PaqueteEnvio) datosEntrada.readObject();

                nick = paqueteEntrante.getNick();
                ip = paqueteEntrante.getIp();
                mensaje = paqueteEntrante.getMensaje();

              /*  DataInputStream flujoEntrada = new DataInputStream(miSocket.getInputStream());
                String mensaje_texto = flujoEntrada.readUTF();
                textArea.append("\n" + mensaje_texto); */

                textArea.append("\n"+"De: ["+nick+"]  Para-> ["+ip+"] "+"   Mensaje: "+ mensaje);

                //----------------------puente por donde va a viajar la info--------------------------------------
                Socket enviarDestinatario = new Socket(ip,9090);

                //------------enviar el paquete---------------
                ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviarDestinatario.getOutputStream());

                paqueteReenvio.writeObject(paqueteEntrante);

                enviarDestinatario.close();
                miSocket.close();
                datosEntrada.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error"+e.getMessage());
        }

    }

    private JTextArea textArea;
    private JScrollPane scrollPane;
}
