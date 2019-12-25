package example.services;

import example.entities.Author;
import example.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorsService {

    @Autowired
    private AuthorRepository repository;

    public List<Author> findAll(){
        return repository.findAll();
    }

    public Author findById(Integer id){
        return repository.findById(id).get();
    }

    @Transactional
    public boolean save(Author author){
        try{
            repository.save(author);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Transactional
    public void delete(Integer id){
        repository.deleteById(id);
    }


}
