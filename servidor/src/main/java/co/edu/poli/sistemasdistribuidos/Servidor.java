/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package co.edu.poli.sistemasdistribuidos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Servidor {
    static class Mensaje {
        final String cuenta;
        final double valor;

        Mensaje(String cuenta, double valor) {
            this.cuenta = cuenta;
            this.valor = valor;
        }
    }

    private static final String SEPARADOR = "|";

    private static Mensaje convertirLineaAMensaje(String linea) {
        // Separar elementos de la línea usando el separador - se debe "escapar" para que
        // no sea interpretado como una expresión regular.
        String[] elementos = linea.split(Pattern.quote(SEPARADOR));
        // Se verifica que hayan al menos 2 elementos (cuenta y valor)
        if (elementos.length < 2) {
            throw new RuntimeException("formato inválido");
        }

        // La cuenta es el primer elemento
        String cuenta = elementos[0];

        // Convertir el valor (que es el segundo elemento) a tipo double
        double valor;
        try {
            valor = Double.parseDouble(elementos[1]);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("valor inválido: " + elementos[1]);
        }

        // Retornar objeto mensaje
        return new Mensaje(cuenta, valor);
    }

    private static void guardarMensaje(Mensaje mensaje) throws IOException {
        // Concatenar mensaje separado por comas (,)
        String linea = mensaje.cuenta + "," + mensaje.valor + "\n";
        // Abrir el archivo para escribir línea
        try (OutputStream output = new FileOutputStream("./datos.txt")) {
            // Escribir mensaje codificado con UTF-8
            output.write(linea.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void main(String[] args) throws IOException {
        // Escuchar peticiones en el puerto 1234
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Servidor escuchando en el puerto: " + serverSocket.getLocalPort());

            // Recibir conexiones desde el cliente
            while (true) {
                // Esperar conexión del cliente
                try (Socket clientSocket = serverSocket.accept();
                     // Convertir el stream de datos de entrada a un BufferedReader para poder leer línea a línea
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     // Salida para escribir respuesta como string
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
                    System.out.println("Conexión recibida desde: " + clientSocket.getInetAddress());

                    // Leer línea del mensaje de entrada
                    String linea = reader.readLine();
                    System.out.println("Mensaje: " + linea);
                    try {
                        // Convertir línea a tipo Mensaje
                        Mensaje mensaje = convertirLineaAMensaje(linea);
                        // Guardar el mensaje en el archivo
                        guardarMensaje(mensaje);
                        writer.write("OK");
                    } catch (RuntimeException rte) {
                        System.err.println(rte.getMessage());
                        writer.write("NO-OK");
                    }

                    // Terminar respuesta escribiendo una nueva línea
                    writer.newLine();
                    System.out.println("Respuesta enviada");
                }
            }
        }
    }
}