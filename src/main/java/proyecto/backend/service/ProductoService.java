package proyecto.backend.service;

import proyecto.backend.dto.ProductoDto;
import proyecto.backend.dto.UserDetailDto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<ProductoDto> getAllProductos() throws Exception;

    Optional<ProductoDto> getProductoById(int id) throws Exception;

    boolean updateProducto(ProductoDto productoDto) throws Exception;

    boolean deleteProductoById(int id) throws Exception;

    boolean addProducto(ProductoDto productoDto) throws Exception;

}
