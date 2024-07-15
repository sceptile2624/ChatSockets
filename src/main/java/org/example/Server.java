package org.example;

import java.io.BufferedReader; // para leer texto de una secuencia de entrada
import java.io.DataOutputStream; // para escribir datos primitivos a una secuencia de salida
import java.io.IOException; // para manejar excepciones de entrada/salida
import java.io.InputStreamReader; // para leer texto de una secuencia de entrada
import java.net.ServerSocket; // para crear un socket del servidor
import java.net.Socket; // para crear un socket

public class Server {

    private ServerSocket serverSocket; // socket del servidor
    private Socket clientSocket; // socket del cliente
    private DataOutputStream outputClient; // para enviar datos al cliente
    private BufferedReader inputClient; // para leer datos del cliente
    private BufferedReader consoleInput; // para leer datos desde la consola del servidor
    private String message; // mensaje que se envia o recibe

    public Server() throws IOException {
        serverSocket = new ServerSocket(1234); // crea un socket del servidor en el puerto 1234
        consoleInput = new BufferedReader(new InputStreamReader(System.in)); // inicializa la lectura de la consola
    }

    public void startServer() {
        try {
            System.out.println("Esperando conexión del cliente...");
            clientSocket = serverSocket.accept(); // espera y acepta una conexion del cliente
            System.out.println("Cliente en línea...");

            outputClient = new DataOutputStream(clientSocket.getOutputStream()); // inicializa la salida de datos al cliente
            inputClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // inicializa la entrada de datos del cliente

            Thread sendMessage = new Thread(() -> { // hilo para enviar mensajes
                try {
                    String message;
                    while (true) {
                        message = consoleInput.readLine(); // lee un mensaje de la consola
                        outputClient.writeUTF(message); // lo envia al cliente
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje: " + e.getMessage());
                }
            });

            Thread readMessage = new Thread(() -> { // hilo para leer mensajes
                try {
                    String message;
                    while (true) {
                        message = inputClient.readLine(); // lee un mensaje del cliente
                        if (message != null) {
                            System.out.println("Cliente: " + message); // imprime el mensaje del cliente
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error al leer mensaje: " + e.getMessage());
                }
            });

            sendMessage.start(); // inicia el hilo para enviar mensajes
            readMessage.start(); // inicia el hilo para leer mensajes

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close(); // cierra el servidor
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el servidor: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(); // crea una instancia del servidor
        System.out.println("Iniciando servidor...");
        server.startServer(); // inicia el servidor
    }
}