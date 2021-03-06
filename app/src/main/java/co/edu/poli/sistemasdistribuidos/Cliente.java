package co.edu.poli.sistemasdistribuidos;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Clase con interfaz que permite al usuario crear y consultar cuentas mediante
 * el envío de peticiones al servidor
 */
public class Cliente {
    private static final String MENU = "Ingrese una opción:\n1) Crear cuenta\n2) Consultar cuenta\n9) Salir\n> ";

    // leer string hasta que no sea vacío
    private static String leerString(Scanner scanner) {
        String str;
        do {
            str = scanner.nextLine();
        } while (str.isBlank());
        return str;
    }

    private static Mensaje leerCrearCuentaMensaje(Scanner scanner) {
        // Solicitar cuenta
        System.out.print("Cuenta: ");
        String cuenta = leerString(scanner);
        // Solicitar valor
        System.out.print("Valor: ");
        double valor = scanner.nextDouble();

        return new Mensaje(Mensaje.Tipo.CrearCuenta, cuenta, valor);
    }

    private static Mensaje leerConsultarCuentaMensaje(Scanner scanner) {
        // Solicitar cuenta
        System.out.print("Cuenta: ");
        String cuenta = leerString(scanner);
        return new Mensaje(Mensaje.Tipo.ConsultarCuenta, cuenta, -1);
    }

    private static void enviarMensaje(String host, String mensaje) throws IOException {
        try (
                // Conectarse al servidor en el puerto 1234
                Socket socket = new Socket(host, 1234);
                // Writer para enviar el mensaje
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                // Reader para recibir el resultado
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Enviar mensaje
            writer.write(mensaje + "\n");
            writer.flush();

            // Leer resultado
            String respuesta = reader.readLine();
            System.out.println("Respuesta: " + respuesta);
        } catch (ConnectException ce) {
            System.err.println("No ha sido posible conectarse al servidor: " + ce.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        // Por defecto, el servidor se ejecuta en la misma maquina
        String host = "localhost";
        // Si se especifica un argumento, usarlo como la dirección del servidor
        if (args.length >= 1) {
            host = args[0];
        }

        // Crear escáner para leer datos de la consola
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(MENU);
                int opcion = scanner.nextInt();
                if (opcion == 9) {
                    break;
                }

                Mensaje mensaje;
                switch (opcion) {
                    case 1:
                        mensaje = leerCrearCuentaMensaje(scanner);
                        break;
                    case 2:
                        mensaje = leerConsultarCuentaMensaje(scanner);
                        break;
                    default:
                        System.err.println("opcion inválida: " + opcion);
                        continue;
                }

                enviarMensaje(host, mensaje.encode());
            }
        }
    }
}
