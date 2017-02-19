package pet.wifispots.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pet.wifispots.data.model.Category;
import pet.wifispots.data.repository.CategoryRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    List<Category> categories;

    @PostConstruct
    public void onInit() {
        categories = categoryRepository.findAll();
    }

    public List<Category> getCategories() {
        return categories;
    }
}
