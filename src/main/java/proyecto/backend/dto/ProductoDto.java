package proyecto.backend.dto;

public record ProductoDto(Integer codPro,
                          String nomPro,
                          String descripcion,
                          Double precio,
                          Integer stockPro,
                          String catPro) {
}
