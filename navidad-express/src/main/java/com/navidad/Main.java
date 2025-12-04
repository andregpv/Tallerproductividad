package com.navidad;

import com.navidad.service.NavidadService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NavidadService svc = new NavidadService();
        svc.initSampleTrajesIfEmpty();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Navidad Express MTY ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Crear renta");
            System.out.println("3. Registrar pago");
            System.out.println("4. Cambiar estatus de traje (separado/entregado/regresado)");
            System.out.println("5. Listar rentas");
            System.out.println("6. Reportes (filtrar)");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1" -> svc.registerClientInteractive(sc);
                case "2" -> svc.createRentInteractive(sc);
                case "3" -> svc.recordPaymentInteractive(sc);
                case "4" -> svc.changeRentStatusInteractive(sc);
                case "5" -> svc.listRents();
                case "6" -> svc.reportsInteractive(sc);
                case "0" -> {
                    System.out.println("Saliendo...");
                    svc.shutdown();
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }
}
