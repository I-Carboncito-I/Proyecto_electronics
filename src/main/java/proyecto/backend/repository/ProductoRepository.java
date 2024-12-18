package proyecto.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Aquí puedes agregar consultas personalizadas si es necesario
}


