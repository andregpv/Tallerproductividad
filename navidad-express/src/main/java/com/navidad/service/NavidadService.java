package com.navidad.service;

import com.google.gson.reflect.TypeToken;
import com.navidad.model.*;
import com.navidad.repo.JsonRepository;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class NavidadService {

    private final JsonRepository<Client> clientRepo;
    private final JsonRepository<Traje> trajeRepo;
    private final JsonRepository<Rent> rentRepo;
    private final JsonRepository<Payment> paymentRepo;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("d/MM/yyyy");

    public NavidadService() {
        Type tClient = new TypeToken<List<Client>>(){}.getType();
        Type tTraje = new TypeToken<List<Traje>>(){}.getType();
        Type tRent = new TypeToken<List<Rent>>(){}.getType();
        Type tPayment = new TypeToken<List<Payment>>(){}.getType();

        clientRepo = new JsonRepository<>("data/clients.json", tClient);
        trajeRepo = new JsonRepository<>("data/trajes.json", tTraje);
        rentRepo = new JsonRepository<>("data/rents.json", tRent);
        paymentRepo = new JsonRepository<>("data/payments.json", tPayment);
    }

    // Init sample trajes if none exist
    public void initSampleTrajesIfEmpty() {
        List<Traje> trajes = trajeRepo.readAll();
        if (trajes == null || trajes.isEmpty()) {
            trajes = new ArrayList<>();
            trajes.add(new Traje("Santa Clásico", "Rojo", 450));
            trajes.add(new Traje("Santa Azul", "Azul", 450));
            trajes.add(new Traje("Grinch", "Verde", 400));
            trajes.add(new Traje("Duende", "Verde claro", 350));
            trajeRepo.writeAll(trajes);
            System.out.println("Se añadieron trajes de ejemplo.");
        }
    }

    public void registerClientInteractive(Scanner sc) {
        System.out.print("Nombre del cliente: ");
        String name = sc.nextLine().trim();
        System.out.print("Teléfono: ");
        String phone = sc.nextLine().trim();
        Client c = new Client(name, phone);
        List<Client> clients = clientRepo.readAll();
        clients.add(c);
        clientRepo.writeAll(clients);
        System.out.println("Cliente registrado: " + c);
    }

    public void createRentInteractive(Scanner sc) {
        List<Client> clients = clientRepo.readAll();
        if (clients.isEmpty()) { System.out.println("No hay clientes. Registra primero."); return; }
        System.out.println("Clientes:");
        clients.forEach(c -> System.out.println(c.toString()));
        System.out.print("Ingrese id (primeros 6 caracteres) del cliente: ");
        String cidShort = sc.nextLine().trim();
        Optional<Client> cOpt = clients.stream().filter(c -> c.getId().startsWith(cidShort)).findFirst();
        if (cOpt.isEmpty()) { System.out.println("Cliente no encontrado."); return; }
        Client client = cOpt.get();

        List<Traje> trajes = trajeRepo.readAll();
        System.out.println("Trajes disponibles:");
        trajes.forEach(t -> System.out.println(t.toString()));
        System.out.print("Ingrese id (primeros 6 caracteres) del traje: ");
        String tidShort = sc.nextLine().trim();
        Optional<Traje> tOpt = trajes.stream().filter(t -> t.getId().startsWith(tidShort)).findFirst();
        if (tOpt.isEmpty()) { System.out.println("Traje no encontrado."); return; }
        Traje traje = tOpt.get();

        try {
            System.out.print("Fecha inicio (d/MM/yyyy): ");
            LocalDate from = LocalDate.parse(sc.nextLine().trim(), df);
            System.out.print("Fecha fin (d/MM/yyyy): ");
            LocalDate to = LocalDate.parse(sc.nextLine().trim(), df);
            if (to.isBefore(from)) { System.out.println("Fecha fin antes de inicio."); return; }

            // Verificar disponibilidad: no debe existir otra renta para ese traje que se solape y esté separado o entregado
            List<Rent> rents = rentRepo.readAll();
            boolean conflict = rents.stream()
                    .filter(r -> r.getTrajeId().equals(traje.getId()))
                    .anyMatch(r -> !(r.getToDate().isBefore(from) || r.getFromDate().isAfter(to))); // overlap
            if (conflict) {
                System.out.println("El traje no está disponible en esas fechas.");
                return;
            }

            double total = traje.getPrice(); // simple: precio por renta fija
            Rent rent = new Rent(client.getId(), traje.getId(), from, to, total);
            rents.add(rent);
            rentRepo.writeAll(rents);
            System.out.println("Renta creada: id=" + rent.getId().substring(0,6) + " Total=$" + total);
            System.out.println("Saldo pendiente: $" + rent.getBalance());
        } catch (Exception ex) {
            System.out.println("Error en fechas: " + ex.getMessage());
        }
    }

    public void recordPaymentInteractive(Scanner sc) {
        List<Rent> rents = rentRepo.readAll();
        if (rents.isEmpty()) { System.out.println("No hay rentas."); return; }
        System.out.println("Rentas:");
        rents.forEach(r -> System.out.println("[" + r.getId().substring(0,6) + "] Cliente:" + r.getClientId().substring(0,6) + " Traje:" + r.getTrajeId().substring(0,6) + " $" + r.getTotal() + " Pagado:$" + r.getPaid() + " Saldo:$" + r.getBalance() + " Estatus:" + r.getStatus()));
        System.out.print("Ingrese id (primeros 6 caracteres) de la renta: ");
        String ridShort = sc.nextLine().trim();
        Optional<Rent> rOpt = rents.stream().filter(r -> r.getId().startsWith(ridShort)).findFirst();
        if (rOpt.isEmpty()) { System.out.println("Renta no encontrada."); return; }
        Rent rent = rOpt.get();
        System.out.print("Cantidad a pagar: ");
        try {
            double amt = Double.parseDouble(sc.nextLine().trim());
            if (amt <= 0) { System.out.println("Cantidad inválida."); return; }
            // registrar pago
            List<Payment> payments = paymentRepo.readAll();
            Payment p = new Payment(rent.getId(), amt);
            payments.add(p);
            paymentRepo.writeAll(payments);

            // actualizar renta
            rent.addPayment(amt);
            rentRepo.writeAll(rents);

            System.out.println("Pago registrado. Saldo restante: $" + rent.getBalance());
            if (rent.getBalance() <= 0.001) System.out.println("La renta está liquidada.");
        } catch (NumberFormatException ex) {
            System.out.println("Cantidad inválida.");
        }
    }

    public void changeRentStatusInteractive(Scanner sc) {
        List<Rent> rents = rentRepo.readAll();
        if (rents.isEmpty()) { System.out.println("No hay rentas."); return; }
        rents.forEach(r -> System.out.println("[" + r.getId().substring(0,6) + "] Estatus:" + r.getStatus() + " Fechas:" + r.getFromDate() + "-" + r.getToDate()));
        System.out.print("Ingrese id (primeros 6) de la renta: ");
        String ridShort = sc.nextLine().trim();
        Optional<Rent> rOpt = rents.stream().filter(r -> r.getId().startsWith(ridShort)).findFirst();
        if (rOpt.isEmpty()) { System.out.println("Renta no encontrada."); return; }
        Rent rent = rOpt.get();
        System.out.print("Nuevo estatus (separado/entregado/regresado): ");
        String st = sc.nextLine().trim().toLowerCase();
        if (!List.of("separado","entregado","regresado").contains(st)) { System.out.println("Estatus inválido."); return; }
        rent.setStatus(st);
        rentRepo.writeAll(rents);
        System.out.println("Estatus actualizado.");
    }

    public void listRents() {
        List<Rent> rents = rentRepo.readAll();
        Map<String, Client> clientMap = clientRepo.readAll().stream().collect(Collectors.toMap(Client::getId, c -> c));
        Map<String, Traje> trajeMap = trajeRepo.readAll().stream().collect(Collectors.toMap(Traje::getId, t -> t));
        if (rents.isEmpty()) { System.out.println("No hay rentas registradas."); return; }
        rents.forEach(r -> {
            Client c = clientMap.get(r.getClientId());
            Traje t = trajeMap.get(r.getTrajeId());
            System.out.printf("[%s] Cliente:%s Traje:%s Fechas:%s-%s Total:$%.2f Pagado:$%.2f Saldo:$%.2f Estatus:%s%n",
                    r.getId().substring(0,6),
                    c != null ? c.getName() : r.getClientId().substring(0,6),
                    t != null ? t.getName() : r.getTrajeId().substring(0,6),
                    r.getFromDate().format(df),
                    r.getToDate().format(df),
                    r.getTotal(), r.getPaid(), r.getBalance(), r.getStatus());
        });
    }

    public void reportsInteractive(Scanner sc) {
        System.out.println("Reportes: 1) Filtrar por fecha  2) Filtrar por traje  3) Pendientes de pago");
        System.out.print("Opción: ");
        String o = sc.nextLine().trim();
        List<Rent> rents = rentRepo.readAll();
        switch (o) {
            case "1" -> {
                try {
                    System.out.print("Fecha (d/MM/yyyy): ");
                    LocalDate f = LocalDate.parse(sc.nextLine().trim(), df);
                    rents.stream().filter(r -> !r.getFromDate().isAfter(f) && !r.getToDate().isBefore(f)).forEach(r -> System.out.println(r.getId().substring(0,6) + " " + r.getStatus() + " " + r.getFromDate() + "-" + r.getToDate()));
                } catch (Exception e) { System.out.println("Formato inválido."); }
            }
            case "2" -> {
                List<Traje> trajes = trajeRepo.readAll();
                trajes.forEach(t -> System.out.println(t.toString()));
                System.out.print("Id (6) traje: ");
                String tid = sc.nextLine().trim();
                rents.stream().filter(r -> r.getTrajeId().startsWith(tid)).forEach(r -> System.out.println(r.getId().substring(0,6) + " " + r.getStatus()));
            }
            case "3" -> {
                rents.stream().filter(r -> r.getBalance() > 0.001).forEach(r -> System.out.println(r.getId().substring(0,6) + " Saldo:$" + r.getBalance()));
            }
            default -> System.out.println("Opción inválida.");
        }
    }

    public void shutdown() {
        // placeholder if se necesita liberar recursos
    }
}
