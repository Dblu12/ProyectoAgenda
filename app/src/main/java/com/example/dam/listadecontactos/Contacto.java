package com.example.dam.listadecontactos;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Contacto implements Serializable, Comparable<Contacto>{
    private int id;

    // Telefono es declarado como String para evitar el error tipico del +34.

    private String nombre;
    private List <String> telefonos;

    public Contacto(int id, String nombre, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = telefonos;
    }

    public Contacto() {
        this(0,"", new ArrayList<String>());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefonos=" + telefonos +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return nombre.equals(contacto.nombre);

    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    @Override
    public int compareTo(Contacto another) {
        int r= this.nombre.compareToIgnoreCase(another.nombre);
        if(r==0){
            r= (int) (this.id - another.id);
        }
        return r;
    }

    public String getTelefono(int location) {
        return telefonos.get(location);
    }

    public void setTelefono(int location, String telefono){
        this.telefonos.set(location, telefono);
    }

    public boolean add(String object) {
        return telefonos.add(object);
    }

    public int size() {
        return telefonos.size();
    }

    public boolean isEmpty() {
        return telefonos.isEmpty();
    }

    public void eliminarRepetidos(){
        Collections.sort(telefonos);
        for(int s=1; s<telefonos.size(); s++){
            if(telefonos.get(s).equals(telefonos.get(s-1))){
                telefonos.remove(s);
            }
        }
    }
}
