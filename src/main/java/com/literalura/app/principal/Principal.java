package com.literalura.app.principal;

import com.literalura.app.controller.LibroController;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Principal {
    private final Scanner teclado;
    private final LibroController libroController;

    public Principal(LibroController libroController) {
        this.libroController = libroController;
        this.teclado = new Scanner(System.in);
    }

    public void muestraElMenu() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("""
                    
                    =============================
                    📚 Bienvenido a LiterAlura - MENU PRINCIPAL
                    Seleccione una opción:
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un año
                    5 - Listar libros por idioma
                    0 - Salir
                    =============================
                    """);
            try {
                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
                    case 1 -> libroController.buscarLibro();
                    case 2 -> libroController.mostrarLibros();
                    case 3 -> libroController.listarAutoresRegistrados();
                    case 4 -> libroController.listarAutoresVivos();
                    case 5 -> libroController.listarLibrosPorIdioma();
                    case 0 -> System.out.println("👋 ¡Gracias por usar LiterAlura!");
                    default -> System.out.println("❗ Opción no válida. Intente de nuevo.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❗ Por favor, ingrese un número válido.");
            }
        }
    }
}
