package example.services;

import example.entities.Book;
import example.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService  {


    @Autowired
    private BookRepository repository;

    public List<Book> findAll(){
        return repository.findAll();
    }

    public Book findById(Integer id){
        return repository.findById(id).get();
    }

    @Transactional
    public boolean save(Book book){
        try{
            repository.save(book);
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
