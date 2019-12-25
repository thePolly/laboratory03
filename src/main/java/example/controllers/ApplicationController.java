package example.controllers;

import example.entities.Author;
import example.entities.Book;
import example.services.AuthorsService;
import example.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class ApplicationController {

    @Autowired
    AuthorsService authorsService;

    @Autowired
    BookService bookService;

    @RequestMapping(value = { "/index"}, method = RequestMethod.GET)
    public String watchIndex(Model model){
        return "index";
    }



    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public String watchAuthors(Model model){
        List<Author> groups = authorsService.findAll();
        model.addAttribute("authors", groups);
        return "authors";
    }
    @RequestMapping(value = "/createAuthor", method = RequestMethod.POST)
    public ModelAndView createAuthor(ModelMap model, @RequestParam("firstName") String firstName,
                                    @RequestParam("lastName") String lastName,
                                    @RequestParam("yearOfBirth") String yearOfBirth){
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setYearOfBirth(Integer.valueOf(yearOfBirth));
        boolean saveStatus = authorsService.save(author);
        String status = saveStatus ? "redirect:/authors" : "redirect:/index";
        return new ModelAndView(status, model);
    }

    @RequestMapping(value = "/deleteAuthor", method = RequestMethod.POST)
    public ModelAndView deleteAuthor(ModelMap model, @RequestParam("id") String id){
        authorsService.delete(Integer.valueOf(id));
        return new ModelAndView("redirect:/authors", model);
    }


    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public String watchBooks(Model model){
        List<Book> bookList = bookService.findAll();
        model.addAttribute("bookslist", bookList);
        return "books";
    }
    @RequestMapping(value = "/createBook", method = RequestMethod.POST)
    public ModelAndView createBook(ModelMap model, @RequestParam("name") String name,
                                     @RequestParam("year") String year,
                                     @RequestParam("authorId") String authorId,
                                     @RequestParam("summery") String summery)
    {
        Book book = new Book();
        book.setName(name);
        book.setYear(Integer.valueOf(year));
        book.setSummary(summery);
        Author author = authorsService.findById(Integer.valueOf(authorId));
        if (author!=null)
            book.setAuthor(author);
        boolean saveStatus = bookService.save(book);
        String status = saveStatus ? "redirect:/books" : "redirect:/index";
        return new ModelAndView(status, model);
    }
    @RequestMapping(value = "/moreinfo", method = RequestMethod.GET)
    public String watchMoreInfo(Model model, @RequestParam("id") String id){
        Book book = bookService.findById(Integer.valueOf(id));
        Author author = authorsService.findById(Integer.valueOf(book.getAuthor().getAuthorId()));
        model.addAttribute("book", book);
        model.addAttribute("author", author);
        return "moreinfo";
    }

    @RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
    public ModelAndView deleteBook(ModelMap model, @RequestParam("id") String id){
        bookService.delete(Integer.valueOf(id));
        return new ModelAndView("redirect:/books", model);
    }
    @RequestMapping(value = "/bookUpdate", method = RequestMethod.POST)
    public ModelAndView updateBook(ModelMap model, @RequestParam("id") String id, @RequestParam("name") String name,
                                      @RequestParam("year") String year, @RequestParam("author") String author1,
                                      @RequestParam("summary") String summary){

        Book book = bookService.findById(Integer.valueOf(id));
        book.setName(name);
        book.setYear(Integer.valueOf(year));
        book.setSummary(summary);
        Author author = authorsService.findById(Integer.valueOf(author1));
        if (author!=null)
            book.setAuthor(author);
        bookService.save(book);
        return new ModelAndView("redirect:/books", model);
    }


    @RequestMapping(value = "/bookUpdate", method = RequestMethod.GET)
    public String updateBook2(Model model, @RequestParam("id") String id){
        Book book = bookService.findById(Integer.valueOf(id));
        model.addAttribute("book", book);
        return "bookupdate";
    }

    @RequestMapping(value = "/authorUpdate", method = RequestMethod.POST)
    public ModelAndView updateAuthor(ModelMap model, @RequestParam("id") String id,
                                   @RequestParam("name") String name,
                                   @RequestParam("lastname") String lastName, @RequestParam("yearOfBirth") String yearOfBirth){

        Author author = authorsService.findById(Integer.valueOf(id));
       author.setFirstName(name);
       author.setLastName(lastName);
       author.setYearOfBirth(Integer.valueOf(yearOfBirth));
    authorsService.save(author);
        return new ModelAndView("redirect:/authors", model);
    }


    @RequestMapping(value = "/authorUpdate", method = RequestMethod.GET)
    public String updateAuthor2(Model model, @RequestParam("id") String id){
        Author author = authorsService.findById(Integer.valueOf(id));
        model.addAttribute("author", author);
        return "authorupdate";
    }


}
