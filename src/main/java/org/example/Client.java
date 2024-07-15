package org.example;

import java.io.BufferedReader; // para leer texto de una secuencia de entrada
import java.io.DataOutputStream; // para escribir datos primitivos a una secuencia de salida
import java.io.IOException; // para manejar excepciones de entrada/salida
import java.io.InputStreamReader; // para leer texto de una secuencia de entrada
import java.net.Socket; // para crear un socket

public class Client {
    private Socket serverSocket; // socket del servidor
    private DataOutputStream outputServer; // para enviar datos al servidor
    private BufferedReader inputServer; // para leer datos del servidor
    private BufferedReader consoleInput; // para leer datos desde la consola del cliente

    public Client() throws IOException {
        serverSocket = new Socket("192.168.201.102", 1234); // crea un socket del cliente y se conecta al servidor en la dirección IP y puerto especificados
        outputServer = new DataOutputStream(serverSocket.getOutputStream()); // inicializa la salida de datos al servidor
        inputServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream())); // inicializa la entrada de datos del servidor
        consoleInput = new BufferedReader(new InputStreamReader(System.in)); // inicializa la lectura de la consola del cliente
    }

    public void startClient() {
        try {
            System.out.println("Cliente conectado al servidor...");

            Thread sendMessage = new Thread(() -> { // hilo para enviar mensajes
                try {
                    String message;
                    while (true) {
                        message = consoleInput.readLine(); // lee un mensaje de la consola
                        outputServer.writeUTF(message); // lo envia al servidor
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje: " + e.getMessage());
                }
            });

            Thread readMessage = new Thread(() -> { // hilo para leer mensajes
                try {
                    String message;
                    while (true) {
                        message = inputServer.readLine(); // lee un mensaje del servidor
                        if (message != null) {
                            System.out.println("Servidor: " + message); // imprime el mensaje del servidor
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error al leer mensaje: " + e.getMessage());
                }
            });

            sendMessage.start(); // inicia el hilo para enviar mensajes
            readMessage.start(); // inicia el hilo para leer mensajes

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close(); // cierra el socket del cliente
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el cliente: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(); // crea una instancia del cliente
        System.out.println("Iniciando cliente...");
        client.startClient(); // inicia el cliente
    }
}