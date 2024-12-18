package proyecto.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proyecto.backend.dto.ProductoDto;
import proyecto.backend.service.ProductoService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping("/productoslist")
    public String start(Model model) {
        try {
            List<ProductoDto> productos = productoService.getAllProductos();
            model.addAttribute("productos", productos);
            model.addAttribute("error", "");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al obtener los datos del producto");
        }
        return "producto/productos_lista";
    }

    @GetMapping("/detalle/{id}")
    public String getProductoById(@PathVariable("id") int id, Model model) {
        try {
            Optional<ProductoDto> producto = productoService.getProductoById(id);
            if (producto.isPresent()) {
                model.addAttribute("producto", producto.get());
                model.addAttribute("error", "");
            } else {
                model.addAttribute("error", "No se encontró el producto con ID: " + id);
                return "producto/producto_no_encontrado";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al obtener los detalles del producto");
            return "producto/error";
        }
        return "producto/producto_detalle";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agregar")
    public String mostrarFormularioAgregar(Model model) {
        model.addAttribute("producto", new ProductoDto(null, "", "", 0.0, 0, ""));
        return "producto/form_agregar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/agregar")
    public String agregarProducto(@ModelAttribute ProductoDto productoDto, Model model) {
        try {
            boolean resultado = productoService.addProducto(productoDto);
            if (resultado) {
                return "redirect:/productos/productoslist";
            } else {
                model.addAttribute("error", "No se pudo agregar el producto");
                return "producto/form_agregar";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al agregar el producto");
            return "producto/form_agregar";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") int id, Model model) {
        try {
            boolean resultado = productoService.deleteProductoById(id);
            if (resultado) {
                return "redirect:/productos/productoslist";
            } else {
                model.addAttribute("error", "No se pudo eliminar el producto con ID: " + id);
                return "producto/error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al eliminar el producto");
            return "producto/error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/update/{id}")
    public String updateProductoForm(@PathVariable("id") int id, Model model) {
        try {
            Optional<ProductoDto> producto = productoService.getProductoById(id);
            if (producto.isPresent()) {
                model.addAttribute("producto", producto.get());
                return "producto/form_actualizar";
            } else {
                model.addAttribute("error", "Producto no encontrado.");
                return "producto/producto_no_encontrado";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al cargar los detalles del producto.");
            return "producto/error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public String updateProducto(@ModelAttribute ProductoDto productoDto, RedirectAttributes redirectAttributes) {
        try {
            boolean isUpdated = productoService.updateProducto(productoDto);
            if (isUpdated) {
                redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el producto.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al actualizar el producto.");
        }
        return "redirect:/productos/productoslist";
    }
}
