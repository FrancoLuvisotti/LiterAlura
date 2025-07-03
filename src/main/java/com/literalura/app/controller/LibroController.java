package com.literalura.app.controller;

import com.literalura.app.entity.Libro;
import com.literalura.app.model.Datos;
import com.literalura.app.model.DatosLibros;
import com.literalura.app.service.ConsumoAPI;
import com.literalura.app.service.ConvierteDatos;
import com.literalura.app.service.LibroService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Controller
public class LibroController {
    private final Scanner teclado;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos conversor;
    private final LibroService libroService;

    public LibroController(LibroService libroService, ConsumoAPI consumoAPI, ConvierteDatos conversor) {
        this.libroService = libroService;
        this.consumoAPI = consumoAPI;
        this.conversor = conversor;
        this.teclado = new Scanner(System.in);
    }

    // Opción 1
    public void buscarLibro() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        String tituloLibro = teclado.nextLine().trim();

        if (tituloLibro.isEmpty()) {
            System.out.println("❗ Debe ingresar un título válido.");
            return;
        }

        String url = URL_BASE + "?search=" + tituloLibro.replace(" ", "%20");
        String json = consumoAPI.obtenerDatos(url);

        if (json == null || json.isBlank()) {
            System.out.println("❗ No se pudo obtener respuesta de la API.");
            return;
        }

        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("📘 Libro encontrado:");
            mostrarLibro(libroBuscado.get());
            libroService.guardarLibro(libroBuscado.get());
        } else {
            System.out.println("❌ Libro no encontrado.");
        }
    }


    // Mostrar libro desde DatosLibros (API)
    public void mostrarLibro(DatosLibros datosLibro) {
        System.out.println("Título: " + datosLibro.titulo());
        if (!datosLibro.autor().isEmpty()) {
            System.out.println("Autor: " + datosLibro.autor().get(0).nombre());
        } else {
            System.out.println("Autor: Desconocido");
        }
        System.out.println("Idiomas: " + datosLibro.idiomas());
        System.out.println("Descargas: " + datosLibro.numeroDeDescargas());
        System.out.println("---------------------------------------------");
    }

    // Opción 2
    public void mostrarLibros() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros guardados.");
        } else {
            System.out.println("📚 Libros guardados:");
            libros.forEach(this::mostrarLibro);
        }
    }

    // Mostrar libro desde entidad Libro (DB)
    public void mostrarLibro(Libro libro) {
        System.out.println("Título: " + libro.getTitulo());
        if (!libro.getAutores().isEmpty()) {
            System.out.println("Autor: " + libro.getAutores().get(0).getNombre());
        } else {
            System.out.println("Autor: Desconocido");
        }
        System.out.println("Idiomas: " + libro.getIdiomas());
        System.out.println("Descargas: " + libro.getNumeroDeDescargas());
        System.out.println("---------------------------------------------");
    }

    // Opción 3
    public void listarAutoresRegistrados() {
        var autores = libroService.obtenerTodosLosAutores();

        if (autores.isEmpty()) {
            System.out.println("❌ No hay autores registrados.");
        } else {
            System.out.println("👨‍💼 Autores registrados:");
            autores.forEach(autor -> {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getFechaDeNacimiento());
                System.out.println("Fallecimiento: " + autor.getAnoFallecimiento());
                System.out.println("--------------------------------------");
            });
        }
    }

    // Opción 4
    public void listarAutoresVivos() {
        System.out.println("Ingrese el año para filtrar autores vivos:");
        int anio = Integer.parseInt(teclado.nextLine());

        var autores = libroService.obtenerTodosLosAutores();
        var autoresVivos = autores.stream()
                .filter(autor -> {
                    try {
                        int nacimiento = Integer.parseInt(autor.getFechaDeNacimiento());
                        String fallecimientoStr = autor.getAnoFallecimiento();
                        int fallecimiento = (fallecimientoStr == null || fallecimientoStr.isBlank())
                                ? Integer.MAX_VALUE
                                : Integer.parseInt(fallecimientoStr);
                        return nacimiento <= anio && fallecimiento >= anio;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("❌ No se encontraron autores vivos en el año " + anio);
        } else {
            System.out.println("🧓 Autores vivos en el año " + anio + ":");
            autoresVivos.forEach(autor -> {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getFechaDeNacimiento());
                System.out.println("Fallecimiento: " + autor.getAnoFallecimiento());
                System.out.println("--------------------------------------");
            });
        }
    }

    // Opción 5
    public void listarLibrosPorIdioma() {
        System.out.println("Ingrese el código de idioma (ej: es, en, fr, pt):");
        String idioma = teclado.nextLine().toLowerCase();

        List<Libro> libros = libroService.buscarPorIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("❌ No se encontraron libros en el idioma " + idioma);
        } else {
            System.out.println("📚 Libros encontrados en el idioma " + idioma + ":");
            libros.forEach(this::mostrarLibro);
        }
    }
}
