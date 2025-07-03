package com.literalura.app.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
