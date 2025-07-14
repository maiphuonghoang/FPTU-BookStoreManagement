
package com.labia.bookstoremanagement.controller;


import com.labia.bookstoremanagement.configuration.JwtTokenFilter;
import com.labia.bookstoremanagement.model.Book;
import com.labia.bookstoremanagement.model.User;
import com.labia.bookstoremanagement.repository.PageImpl;
import com.labia.bookstoremanagement.repository.UserRepository;
import com.labia.bookstoremanagement.repository.UserRepositoryImpl;
import com.labia.bookstoremanagement.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.List;




@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("")
public class UserSQLi {
    private final UserRepositoryImpl userRepositoryImpl;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    JwtTokenFilter jwtTokenFilter;


    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;




    @Autowired
    public UserSQLi(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }


    @PostMapping("/api/users/update-profile")
    public void updateProfile(@RequestBody User user, HttpServletRequest request) {
        User u = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
        u.setDisplayName(user.getDisplayName());
        u.setDob(user.getDob());
        u.setEmail(user.getEmail());
        userRepositoryImpl.updateUserInformation(u);
    }
    @PostMapping("/api/books/sort")
    public ResponseEntity<Page<Book>> orderby2AllPublic(
            @RequestParam(defaultValue = "bookId") String field,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        String countQuery = "SELECT COUNT(*) FROM Book b WHERE b.isApproved = '1'";
        Query countNativeQuery = entityManager.createNativeQuery(countQuery);
        int total = ((Number) countNativeQuery.getSingleResult()).intValue();
        System.out.println("field" + field);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        String query = "SELECT * FROM Book b WHERE b.isApproved = '1' ORDER BY " + field;
        Query nativeQuery = entityManager.createNativeQuery(query, Book.class);
        nativeQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        nativeQuery.setMaxResults(pageable.getPageSize());
        List<Book> resultList = nativeQuery.getResultList();


        Page<Book> resultPage = new PageImpl<>(resultList, pageable, total);
        return new ResponseEntity<>(resultPage, HttpStatus.OK);
    }
}
