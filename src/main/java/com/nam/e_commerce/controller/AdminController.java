package com.nam.e_commerce.controller;

import com.nam.e_commerce.dto.ProductDTO;
import com.nam.e_commerce.model.Category;
import com.nam.e_commerce.model.Product;
import com.nam.e_commerce.service.CategoryService;
import com.nam.e_commerce.service.ProductService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private static final Logger log = LoggerFactory.getLogger(AdminController.class);
  public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
  @Autowired private CategoryService categoryService;
  @Autowired private ProductService productService;

  @GetMapping
  public String adminHome() {
    return "adminHome";
  }

  @GetMapping("/categories")
  public String getCategories(Model model) {
    model.addAttribute("categories", categoryService.getCategories());
    return "categories";
  }

  @GetMapping("/categories/add")
  public String getAddCategory(Model model) {
    model.addAttribute("category", new Category());
    return "categoriesAdd";
  }

  @PostMapping("/categories/add")
  public String postAddCategory(@ModelAttribute Category category) {
    categoryService.addCategory(category);
    return "redirect:/admin/categories";
  }

  @GetMapping("/categories/delete/{id}")
  public String deleteCategory(@PathVariable int id) {
    categoryService.deleteCategoryById(id);
    return "redirect:/admin/categories";
  }

  @GetMapping("/categories/update/{id}")
  public String getUpdateCategory(@PathVariable int id, Model model) {
    Optional<Category> category = categoryService.getCategoryById(id);
    if (category.isPresent()) {
      model.addAttribute("category", category.get());
      return "categoriesAdd";
    } else {
      return "404";
    }
  }

  // Product Section
  @GetMapping("/products")
  public String getAllProducts(Model model) {
    model.addAttribute("products", productService.getProducts());
    return "products";
  }

  @GetMapping("/products/add")
  public String getAddProduct(Model model) {
    log.info("Get Add Product");
    model.addAttribute("productDTO", new ProductDTO());
    model.addAttribute("categories", categoryService.getCategories());
    return "productsAdd";
  }

  @PostMapping("/products/add")
  public String postAddProduct(
      @ModelAttribute("productDTO") ProductDTO productDTO,
      @RequestParam("productImage") MultipartFile file,
      @RequestParam("imgName") String imgName) throws IOException {
    Product product = new Product();
    product.setId(productDTO.getId());
    product.setName(productDTO.getName());
    product.setPrice(productDTO.getPrice());
    product.setWeight(productDTO.getWeight());
    product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
    product.setDescription(productDTO.getDescription());

    String imageUUID;
    if (!file.isEmpty()) {
      imageUUID = file.getOriginalFilename();
      Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
      Files.write(fileNameAndPath, file.getBytes());
    } else {
      imageUUID = imgName;
    }
    product.setImageName(imageUUID);
    productService.addProduct(product);

    return "redirect:/admin/products";
  }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/product/update/{id}")
    public String getUpdateProduct(@PathVariable long id, Model model) {
        Product product = productService.getProductById(id).get();
        log.info("ProductId: " + product);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setWeight(product.getWeight());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());

        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("productDTO", productDTO);
        return "productsAdd";
    }
}
