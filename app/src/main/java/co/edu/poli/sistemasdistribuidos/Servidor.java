package co.edu.poli.sistemasdistribuidos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

/**
 * Clase que recibe y procesa las peticiones del cliente
 */
public class Servidor {
    private static final String RUTA_ARCHIVO = "./datos.txt";

    private static void crearCuenta(Mensaje mensaje) throws IOException {
        // Concatenar mensaje separado por comas (,)
        String linea = mensaje.cuenta + "," + mensaje.valor + "\n";
        // Abrir el archivo para agregar línea
        try (Writer writer = new FileWriter(RUTA_ARCHIVO, true)) {
            // agregar línea
            writer.write(linea);
        }
    }

    private static double consultarCuenta(String cuenta) throws IOException {
        // Abrir el archivo para leerlo línea a línea
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RUTA_ARCHIVO)))) {
            Optional<Double> valor = reader.lines()
                    // separar cada línea por la coma
                    .map(linea -> linea.split(","))
                    // verificar que haya 2 elementos y filtrar por la cuenta
                    .filter(elementos -> elementos.length == 2 && elementos[0].equals(cuenta))
                    // tomar el valor (segunda posición)
                    .map(elementos -> elementos[1])
                    // convertir el valor a Double
                    .map(Double::parseDouble)
                    // tomar la primera coincidencia
                    .findFirst();

            // si se encontró
            if (valor.isPresent()) {
                // retornar el valor
                return valor.get();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println(fnfe.getMessage());
        }

        throw new RuntimeException("cuenta no encontrada");
    }

    public static void main(String[] args) throws IOException {
        // Escuchar peticiones en el puerto 1234
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Servidor escuchando en el puerto: " + serverSocket.getLocalPort());

            // Recibir conexiones desde el cliente indefinidamente
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
                        Mensaje mensaje = Mensaje.decode(linea);

                        if (mensaje.tipo == Mensaje.Tipo.CrearCuenta) {
                            // Guardar cuenta en el archivo
                            crearCuenta(mensaje);
                            writer.write("OK");
                        } else if (mensaje.tipo == Mensaje.Tipo.ConsultarCuenta) {
                            double valor = consultarCuenta(mensaje.cuenta);
                            writer.write(String.valueOf(valor));
                        }
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
