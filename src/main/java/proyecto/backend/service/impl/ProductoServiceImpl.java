package proyecto.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.backend.dto.ProductoDto;
import proyecto.backend.entity.Producto;
import proyecto.backend.repository.ProductoRepository;
import proyecto.backend.service.ProductoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository; // Suponiendo que tienes un repositorio de productos

    @Override
    public List<ProductoDto> getAllProductos() throws Exception {
        try {
            List<Producto> productos = productoRepository.findAll();
            return productos.stream()
                    .map(producto -> new ProductoDto(
                            producto.getCodPro(),
                            producto.getNomPro(),
                            producto.getDescripcion(),
                            producto.getPrecio(),
                            producto.getStockPro(),
                            producto.getCatPro()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los productos", e);
        }
    }

    @Override
    public Optional<ProductoDto> getProductoById(int id) throws Exception {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            return producto.map(p -> new ProductoDto(
                    p.getCodPro(),
                    p.getNomPro(),
                    p.getDescripcion(),
                    p.getPrecio(),
                    p.getStockPro(),
                    p.getCatPro()
            ));
        } catch (Exception e) {
            throw new Exception("Error al obtener el producto por ID", e);
        }
    }

    @Override
    public boolean updateProducto(ProductoDto productoDto) throws Exception {
        try {
            Optional<Producto> productoOpt = productoRepository.findById(productoDto.codPro());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                producto.setNomPro(productoDto.nomPro());
                producto.setDescripcion(productoDto.descripcion());
                producto.setPrecio(productoDto.precio());
                producto.setStockPro(productoDto.stockPro());
                producto.setCatPro(productoDto.catPro());
                productoRepository.save(producto);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el producto", e);
        }
    }

    @Override
    public boolean deleteProductoById(int id) throws Exception {
        try {
            if (productoRepository.existsById(id)) {
                productoRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el producto", e);
        }
    }

    @Override
    public boolean addProducto(ProductoDto productoDto) throws Exception {
        try {
            Producto producto = new Producto(
                    null, // Para que el id sea autogenerado
                    productoDto.nomPro(),
                    productoDto.descripcion(),
                    productoDto.precio(),
                    productoDto.stockPro(),
                    productoDto.catPro()
            );
            productoRepository.save(producto);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al agregar el producto", e);
        }
    }
}