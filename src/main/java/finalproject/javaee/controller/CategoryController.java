package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.Category;
import finalproject.javaee.model.repository.CategoryRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CategoryController extends BaseController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping(value = "/category/add")
    public void createCategory(@RequestBody Category category, HttpSession session) throws BaseException {
        validateisLoggedIn(session);
        validateCategoryName(category.getCategoryName());
        categoryRepository.save(category);
    }

    @DeleteMapping(value = "/category/delete/{id}")
    public void deleteCategory(@PathVariable("id") long id, HttpSession session) throws BaseException {
        Category category = categoryRepository.findById(id);
        validateisLoggedIn(session);
        if(!categoryRepository.existsById(id)){
            throw new BaseException("Category does not exist.");
        }
        categoryRepository.delete(category);
    }

    @GetMapping(value = "/category")
    public List<Category> allCategories(HttpSession session) throws BaseException{
        validateisLoggedIn(session);
        return categoryRepository.findAll();
    }

    @GetMapping(value = "/category/{id}")
    public Category category(@PathVariable("id") long id,HttpSession session) throws BaseException{
        validateisLoggedIn(session);
        if(!categoryRepository.existsById(id)) {
           throw new BaseException("Category does not exist.");
        }
        return categoryRepository.findById(id);
    }


    /* ************* Validations ************* */

    private void validateCategoryName(String categoryName) throws BaseException{
        if(categoryName == null || categoryName.isEmpty()){
            throw new BaseException("Invalid category name input");
        }
        if(categoryRepository.findByCategoryName(categoryName) != null){
            throw new BaseException("Category name already exists.");
        }
    }


}
