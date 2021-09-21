package co.edu.poli.sistemasdistribuidos;

import java.util.regex.Pattern;

/**
 * Clase que contiene la información del mensaje que el cliente envía al servidor
 */
public class Mensaje {
    public enum Tipo {
        CrearCuenta,
        ConsultarCuenta;

        public static Tipo valueOf(int value) {
            switch (value) {
                case 0:
                    return Tipo.CrearCuenta;
                case 1:
                    return Tipo.ConsultarCuenta;
                default:
                    throw new IllegalArgumentException("tipo inválido: " + value);
            }
        }
    }

    public static final String SEPARADOR = "|";

    final Tipo tipo;
    final String cuenta;
    final double valor;

    Mensaje(Tipo tipo, String cuenta, double valor) {
        this.tipo = tipo;
        this.cuenta = cuenta;
        this.valor = valor;
    }

    // Codificar mensaje en un String para poder enviarlo
    public String encode() {
        // Unir tipo del mensaje, cuenta y valor usando el separador
        return tipo.ordinal() + Mensaje.SEPARADOR + cuenta + Mensaje.SEPARADOR + valor;
    }

    // Crear objeto Mensaje a partir de una línea de texto
    public static Mensaje decode(String linea) {
        // Separar elementos de la línea usando el separador - se debe "escapar" para que
        // no sea interpretado como una expresión regular.
        String[] elementos = linea.split(Pattern.quote(SEPARADOR));

        // El tipo es el primer elemento
        Tipo tipo;
        try {
            tipo = Tipo.valueOf(Integer.parseInt(elementos[0]));
        } catch (IllegalArgumentException ignored) {
            throw new RuntimeException("tipo inválido: " + elementos[0]);
        }

        // Se verifica que haya al menos 2 elementos (tipo y cuenta)
        if (elementos.length < 2) {
            throw new RuntimeException("formato inválido");
        }

        // La cuenta es el segundo elemento
        String cuenta = elementos[1];

        double valor = -1;

        // Al crear la cuenta se requiere el valor;
        if (tipo == Tipo.CrearCuenta) {
            // Convertir el valor (que es el segundo elemento) a tipo double
            if (elementos.length < 3) {
                throw new RuntimeException("formato inválido");
            }

            try {
                valor = Double.parseDouble(elementos[2]);
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("valor inválido: " + elementos[2]);
            }
        }

        // Retornar objeto mensaje
        return new Mensaje(tipo, cuenta, valor);
    }
}
