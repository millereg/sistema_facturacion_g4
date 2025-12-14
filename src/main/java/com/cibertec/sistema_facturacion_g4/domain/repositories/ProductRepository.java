package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
       List<Product> findByCompanyId(Long companyId);

       List<Product> findByCompanyIdAndActiveTrue(Long companyId);

       Optional<Product> findByIdAndCompanyId(Long id, Long companyId);

       Optional<Product> findByCodeAndCompanyId(String code, Long companyId);

       Page<Product> findByCompanyIdAndActiveTrue(Long companyId, Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND " +
                     "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                     "AND p.active = true")
       Page<Product> findByCompanyIdAndSearchTerm(
                     @Param("companyId") Long companyId,
                     @Param("searchTerm") String searchTerm,
                     Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.stock < 5 AND p.stock > 0 AND p.active = true")
       List<Product> findLowStockProductsByCompanyId(@Param("companyId") Long companyId);

       Optional<Product> findByCodeAndActiveTrue(String code);

       Page<Product> findByActiveTrueOrderByName(Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.active = true")
       Page<Product> findProductsWithStock(Pageable pageable);

       @Query("SELECT p FROM Product p WHERE " +
                     "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                     "AND p.active = true")
       Page<Product> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

       Page<Product> findByCategoryIdAndActiveTrueOrderByName(Long categoryId, Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND p.stock > 0 AND p.active = true")
       Page<Product> findByCategoryWithStock(@Param("categoryId") Long categoryId, Pageable pageable);

       @Query("SELECT p FROM Product p WHERE " +
                     "(:searchTerm IS NULL OR " +
                     "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                     "AND (:categoryId IS NULL OR p.categoryId = :categoryId) " +
                     "AND (:onlyWithStock = false OR p.stock > 0) " +
                     "AND p.active = true")
       Page<Product> findWithFilters(@Param("searchTerm") String searchTerm,
                     @Param("categoryId") Long categoryId,
                     @Param("onlyWithStock") Boolean onlyWithStock,
                     Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.stock < 5 AND p.stock > 0 AND p.active = true")
       List<Product> findLowStockProducts();

       Page<Product> findBySupplierIdAndActiveTrueOrderByName(Long supplierId, Pageable pageable);
}