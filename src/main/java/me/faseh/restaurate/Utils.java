package me.faseh.restaurate;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Utils {

    public static final String equal = "=".repeat(70);
    public static final String dash  = "-".repeat(70);

    public static int scnInt(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Entrada vazia. Digite um numero: ");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Digite um numero: ");
            }
        }
    }

    public static double scnDouble(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                if (input.isEmpty()) {
                    System.out.print("Entrada vazia. Digite um valor: ");
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Digite um valor numerico (ex: 32.50): ");
            }
        }
    }

    public static String scnString(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.print("Campo obrigatorio. Digite novamente: ");
        }
    }

    public static String formPreco(double preco) {
        return String.format("R$ %.2f", preco).replace(".", ",");
    }

    public static String scnCom(Scanner scanner) {
        try {
            String input = scanner.nextLine();
            return (input == null) ? "" : input.trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

}
