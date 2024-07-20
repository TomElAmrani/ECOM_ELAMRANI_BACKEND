package com.elamrani.services;

import com.elamrani.entites.Category;
import com.elamrani.dtos.CategoryDTO;
import com.elamrani.dtos.CategoryResponse;

public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
