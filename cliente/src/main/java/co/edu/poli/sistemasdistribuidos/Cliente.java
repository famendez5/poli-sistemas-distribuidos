/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package co.edu.poli.sistemasdistribuidos;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String SEPARADOR = "|";

    public static void main(String[] args) throws IOException {
        // Por defecto, el servidor se ejecuta en la misma maquina
        String host = "localhost";
        // Si se especifica un argumento, usarlo como la dirección del servidor
        if (args.length >= 1) {
            host = args[0];
        }

        // Conectarse al servidor en el puerto 1234
        try (Socket socket = new Socket(host, 1234);
             // Crear escáner para leer datos de la consola
             Scanner scanner = new Scanner(System.in)) {
            // Solicitar cuenta
            System.out.print("Cuenta: ");
            String cuenta = scanner.nextLine();
            // Solicitar valor
            System.out.print("Valor: ");
            String valor = scanner.nextLine();

            // Unir cuenta y valor usando el separador
            String mensaje = cuenta + SEPARADOR + valor + "\n";

            // Para enviar el mensaje
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 // Para leer la respuesta
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                // Enviar mensaje
                writer.write(mensaje);
                writer.flush();

                // Leer respuesta
                String respuesta = reader.readLine();
                System.out.println("Respuesta: " + respuesta);
            }
        } catch (ConnectException ce) {
            System.err.println("No ha sido posible conectarse al servidor: " + ce.getMessage());
        }
    }
}