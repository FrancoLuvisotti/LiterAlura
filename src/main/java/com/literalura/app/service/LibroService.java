package com.literalura.app.service;

import com.literalura.app.entity.Autor;
import com.literalura.app.entity.Libro;
import com.literalura.app.model.DatosLibros;
import com.literalura.app.repository.AutorRepository;
import com.literalura.app.repository.LibroRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void guardarLibro(DatosLibros datos) {
        if (libroRepository.findByTitulo(datos.titulo()).isPresent()) {
            System.out.println("El libro ya está en la base de datos.");
            return;
        }

        List<Autor> autores = datos.autor().stream()
                .map(a -> new Autor(a.nombre(), a.fechaDeNacimiento(), a.anoFallecimiento()))
                .collect(Collectors.toList());

        Libro libro = new Libro(
                datos.titulo(),
                datos.idiomas(),
                datos.numeroDeDescargas(),
                autores
        );

        libroRepository.save(libro);
        System.out.println("Libro guardado exitosamente.");
    }
    public List<Autor> obtenerTodosLosAutores(){
        return autorRepository.findAll();
    }
    public List<Libro> obtenerTodosLosLibros(){
        return libroRepository.findAll();
    }
    public List<Libro> buscarPorIdioma(String idioma){
        return libroRepository.findByIdiomasContainingIgnoreCase(idioma.toLowerCase());
    }
}
