/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.demo.service;


//import tienda.dao.ProductoDao;
import Tienda.demo.domain.Producto;
import Tienda.demo.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class ProductoService {

    // Creamos crea una única instancia de ProductoRepository, y la crea automáticamente
    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) { //Sólo activos.
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

     @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    
@Transactional
public void save(Producto producto, MultipartFile imagenFile) {
    producto = productoRepository.save(producto);
    if (!imagenFile.isEmpty()) { // si está vacío... traerme una imagen...
        try {
            String rutaImagen = firebaseStorageService.uploadImage(
                imagenFile,
                "producto",
                producto.getIdProducto()
            );
            producto.setRutaImagen(rutaImagen);
            productoRepository.save(producto);
        } catch (IOException e) {
            // Manejo de excepción
        }
    }
}



 @Transactional
public void delete(Integer idProducto) {
    if (!productoRepository.existsById(idProducto)) {
        throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
    }
    try {
        productoRepository.deleteById(idProducto);
    } catch (DataIntegrityViolationException e) {
        throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
    }
}

}
