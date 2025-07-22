/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.labia.bookstoremanagement.controller;

import com.labia.bookstoremanagement.configuration.JwtTokenFilter;
import com.labia.bookstoremanagement.configuration.JwtTokenFilter;
import com.labia.bookstoremanagement.model.Book;
import com.labia.bookstoremanagement.model.Category;
import com.labia.bookstoremanagement.model.User;
import com.labia.bookstoremanagement.repository.BookRepository;

import com.labia.bookstoremanagement.repository.CategoryRepository;
import com.labia.bookstoremanagement.repository.UserRepository;
import com.labia.bookstoremanagement.utils.JwtTokenUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author emiukhoahoc
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api/books")
public class BookController {

    int BookId;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    CategoryRepository categoryRepository;

    private final String COVER_UPLOAD_DIR = "/cover/";
    private final String PDF_UPLOAD_DIR = "/pdf/";

    @GetMapping
    List<Book> getAll() {
        return bookRepository.findAll();
    }

    @GetMapping("by-id/{bookId}")
    Book getBookById(@PathVariable("bookId") Integer id, HttpServletRequest request) {
        return bookRepository.findByBookId(id);
    }
//
//   @GetMapping("by-id/{bookId}")
//    Book getBookById(@PathVariable("bookId") Integer id){
//        return bookRepository.findByBookId(id);
//    }

    //
    @GetMapping("/unpublic")
    List<Book> getAllUnPublic() {
        return bookRepository.findByIsApproved(false);
    }

    @GetMapping("/someunpublic")
    List<Book> getSomeUnpublic() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("bookId").descending());
        return bookRepository.findByIsApprovedFalseOrderByBookIdDesc(pageable);
    }

    @GetMapping("by-user")
    ResponseEntity<?> getBookByUser(HttpServletRequest request) {
        try {
            String token = jwtTokenFilter.getJwtFromRequest(request);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            return ResponseEntity.ok(bookRepository.getBookByUsername(username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @GetMapping("find-by-user/{bookId}")
    User getUserOfBook(@PathVariable Integer bookId) {
        return bookRepository.getBookCreated(bookId);
    }

    @GetMapping("by-category/{categoryId}")
    List<Book> getBookByCategory(@PathVariable Integer categoryId) {
        return bookRepository.getBookByCategoryId(categoryId);
    }

    @PostMapping("/executeApi")
    public ResponseEntity<byte[]> executeApi(@RequestBody Map<String, String> requestBody) {
        String apiUrl = requestBody.getOrDefault("api", "");
        try {
            // URL url_updated = new URL(apiUrl);
            // URLConnection connection_updated = url_updated.openConnection();
            byte[] imageBytes = StreamUtils.copyToByteArray(connection.getInputStream());

            // Set appropriate response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Adjust content type based on the image format
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid URL".getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing API".getBytes());
        }
    }

    @GetMapping(value = "/pdf/{fileId}", produces = MediaType.APPLICATION_PDF_VALUE)
    //test ignored issue with comment line 
    //line 153 -> 155
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileId_update, HttpServletRequest request) throws IOException {
        String filePath = "pdf/" + fileId_update + ".pdf";
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileId);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(inputStreamResource);
    }

    @GetMapping(value = "/cover/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileId) throws IOException {
        String filePath = "cover/" + fileId + ".jpg";
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileId);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(inputStreamResource);
    }

    @GetMapping("page")
    public List<Book> getBooks(
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Book> pageBooks = bookRepository.findAll(pageable);
        List<Book> books = pageBooks.getContent();
        return books;
    }

    @GetMapping("publicpage")
    public List<Book> getPublicBooks(
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Book> pageBooks = bookRepository.findByIsApproved(true, pageable);
        List<Book> books = pageBooks.getContent();
        return books;
    }

    @GetMapping("/by-categories/publicpage/{categoryIds}")
    public List<Book> getPageBooksByCategories(
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize,
            @PathVariable("categoryIds") Integer[] categoryIds
    ) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Book> books = bookRepository.getBookByCategoryIds(categoryIds);
        List<Integer> bookIds = new ArrayList<>();
        for (Book book : books) {
            bookIds.add(book.getBookId());
        }
        Page<Book> pageBooks = bookRepository.findByBookIdIn(bookIds, pageable);
//        Page<Book> pageBooks = bookRepository.getBookByBookId(1, pageable);
        List<Book> bookss = pageBooks.getContent();
        return bookss;
    }

    @GetMapping("/pending/page")
    public List<Book> getPagePendingBook(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        Pageable isPageable = PageRequest.of(pageNumber, pageSize);
        Page<Book> pageBooks = bookRepository.findByIsApproved(false, isPageable);
        for (Book book : pageBooks.getContent()) {
            book.setCoverPath(userRepository.getUserByBooks(book).getUsername());
            book.setPdfPath(userRepository.getUserByBooks(book).getDisplayName());
        }
        return pageBooks.getContent();
    }

    @GetMapping("/public/page")
    public List<Book> getPagePublicBook(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        Pageable isPageable = PageRequest.of(pageNumber, pageSize);
        Page<Book> pageBooks = bookRepository.findByIsApproved(true, isPageable);
        for (Book book : pageBooks.getContent()) {
            book.setCoverPath(userRepository.getUserByBooks(book).getUsername());
            book.setPdfPath(userRepository.getUserByBooks(book).getDisplayName());
        }
        return pageBooks.getContent();
    }

    @GetMapping("by-categories/{categoryIds}")
    public List<Book> getBooks(@PathVariable("categoryIds") Integer[] categoryIds) {
        List<Book> books = bookRepository.getBookByCategoryIds(categoryIds);
        return books;
    }

    @GetMapping("pending")
    public List<Book> getPendingBooks() {
        return bookRepository.findByIsApproved(false);
    }

    @GetMapping("public")
    public List<Book> getPublicBooks() {
        List<Book> books = bookRepository.findByIsApproved(true);
        return books;
    }

    @DeleteMapping("delete/{bookId}")
    public void deleteBook(@PathVariable("bookId") int bookId, HttpServletRequest request) {
//<<<<<<< HEAD
//        System.out.println("DELETE BOOK ID CALLED.");
//        try {
//            Book book = bookRepository.findByBookId(bookId);
//            if (book != null) {
//                String username = jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request));
//                User admin = userRepository.userHasRole(username, 2);
//                User superAdmin = userRepository.userHasRole(username, 1);
//                if (admin != null || superAdmin != null) {
//                    bookRepository.deleteBookCategoryByBookId(bookId);
//                    bookRepository.deleteById(bookId);
//                    System.out.println("DELETED.");
//                } else {
//                    System.out.println("UNAUTHORIZED.");
//                }
//            }
//            User bookOwner = bookRepository.getBookCreated(bookId);
//        } catch (Exception e) {
//            System.out.println("EXCEPTION");
//=======
        User user = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
        for (Book b
                : user.getBooks()) {
            if (b.getBookId() == bookId) {
                bookRepository.deleteBookCategoryByBookId(bookId);
                bookRepository.deleteById(bookId);
            }
        }
    }

    @GetMapping("cover/delete")
    public void deleteBook(@RequestParam("fileName") String fileName, HttpServletRequest request) {
        String username = jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request));
        if (username == null) {
            return;
        }
        String filePath = "./cover/" + fileName;
        String command = "rm -rf " + filePath;
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
//            Process process = processBuilder.start();
            String[] cmdArray = { "/bin/bash", "-c", command };
            Process process = Runtime.getRuntime().exec(cmdArray);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Command executed successfully");
            } else {
                System.out.println("Command execution failed");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("pdf/delete")
    public void deletePdfBook(@RequestParam("fileName") String fileName, HttpServletRequest request) {
        String username = jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request));
        if (username == null) {
            return;
        }
        String filePath = "./pdf/" + fileName;
        String command = "rm -rf " + filePath;
        try {
            String[] cmdArray = { "/bin/bash", "-c", command };
            Process process = Runtime.getRuntime().exec(cmdArray);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Command executed successfully");
            } else {
                System.out.println("Command execution failed");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // BROKEN AUTHORIZATION: CHECK USENAME != NULL ONLY
    @PostMapping("approve/{bookId}")
    public void approveBook(@PathVariable("bookId") int bookId, HttpServletRequest request) {
        System.out.println("CALLING APPROVEBOOK API");
        try {
            String username = jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request));
//            User u = userRepository.userHasRole(username, 2);
            if (username != null) {
                bookRepository.updateBookStatus(bookId);
                System.out.println("UPDATED.");
            } else {
                System.out.println("UNAUTHORIZED.");
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/pending/{bookId}")
    public void deletePendingBook(@PathVariable("bookId") int bookId, HttpServletRequest httpServletRequest) {
        Book book = bookRepository.findByBookId(bookId);
        if (book != null) {
            if (!book.isApproved()) {
                try {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(httpServletRequest));
                    if (username != null) {
                        bookRepository.deleteBookCategoryByBookId(bookId);
                        bookRepository.deleteById(bookId);
                        System.out.println("DELETED.");
                    }
                } catch (Exception e) {
                    System.out.println("EXCEPTION: " + e.getMessage());
                }
            }
        } else {
            System.out.println("BOOK IS NULL.");
        }
    }

    static class BookData {
        private Book book;
        private String createdBy;

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public BookData(Book book, String createdBy) {
            this.book = book;
            this.createdBy = createdBy;
        }

        public BookData() {
        }

    }

    /**
     * @param bookData
     * @return
     */
    @PostMapping("add")
    public Book addBook(@RequestBody BookData bookData, HttpServletRequest request) {
        User user = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
        bookData.book.setCreatedBy(user);
        bookRepository.save(bookData.book);
//        Book temp = bookRepository.findByTitle(book.getTitle());
        BookId = bookData.book.getBookId();
        System.out.println(bookData.book.getBookId());
        bookData.book.setCoverPath("cover/" + bookData.book.getBookId() + ".jpg");
        bookData.book.setPdfPath("pdf/" + bookData.book.getBookId() + ".pdf");
        bookRepository.save(bookData.book);

//        categoryRepository.saveBook_Category(41,1);
        for (Category c : bookData.book.getCategories()) {
            categoryRepository.saveBook_Category(bookData.book.getBookId(), c.getCategoryId());
        }
        return bookData.book;
    }

    @PostMapping("/cover/upload")
    public void ploadCoverFile(@RequestParam("coverPath") MultipartFile file, @RequestParam("bookId") String bookId,HttpServletRequest request) {
        User user = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
        if(user == null)return;
        for (Book b:
             user.getBooks()) {
            if(b.getBookId() == Integer.parseInt(bookId)){
                String fileExtension = getFileExtension(file.getOriginalFilename());
                if ((fileExtension.equalsIgnoreCase("jpg")) && file.getSize() < 5000000) {
                    String fileName = StringUtils.cleanPath(bookId + ".jpg");
                    try {
                        // Save the file to the uploads directory
                        String uploadDir = System.getProperty("user.dir") + COVER_UPLOAD_DIR;
                        file.transferTo(new File(uploadDir + fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @PostMapping("/pdf/upload")
    public void ploadPdfFile(@RequestParam("pdfPath") MultipartFile file, @RequestParam("bookId") String bookId,HttpServletRequest request) {
        User user = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
        if(user == null)return;
        for (Book b:
             user.getBooks()) {
            if(b.getBookId() == Integer.parseInt(bookId)){
                String fileExtension = getFileExtension(file.getOriginalFilename());
                if ((fileExtension.equalsIgnoreCase("pdf")) && file.getSize() < 5000000) {
                    String fileName = StringUtils.cleanPath(bookId + ".pdf");
                    try {
                        // Save the file to the uploads directory
                        String uploadDir = System.getProperty("user.dir") + PDF_UPLOAD_DIR;
                        file.transferTo(new File(uploadDir + fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static String getFileExtension(String fileName) {
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }
        return extension;
    }

    //    @DeleteMapping("/delete/{bookId}")
//    public void deleteBook(@PathVariable("bookId") Integer bookId) {
//        categoryRepository.deleteBook_Category(bookId);
//        bookRepository.deleteById(bookId);
//    }
//    @GetMapping("/{bookId}")
//    public Book getBookById(@PathVariable("bookId") Integer bookId) {
//        return bookRepository.findById(bookId).get();
//    }
    @PostMapping("/update/{bookId}")
    Book updateBookById(@PathVariable Integer bookId, @RequestBody Book updateBook, HttpServletRequest request) {
        try {
            User user = userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwtTokenFilter.getJwtFromRequest(request)));
            for (Book b
                    : user.getBooks()) {
                if (b.getBookId() == bookId) {
                    Optional<Book> book = bookRepository.findById(bookId);
                    book.get().setTitle(updateBook.getTitle());
                    book.get().setDescription(updateBook.getDescription());

                    book.get().setAuthorName(updateBook.getAuthorName());

                    if (book.get().getCategories().equals(updateBook.getCategories()))return updateBook;
                    if (updateBook.getCategories().isEmpty()) {
                        categoryRepository.deleteBook_Category(bookId);
                    } else {
                        if (!book.get().getCategories().equals(updateBook.getCategories())) {
                            categoryRepository.deleteBook_Category(bookId);
                            for (Category c : updateBook.getCategories()) {
                                categoryRepository.saveBook_Category(bookId, c.getCategoryId());
                            }
                        }
                    }
                    return bookRepository.save(book.get());
                }
            }

        } catch (Exception e) {
            Optional<Book> book = bookRepository.findById(bookId);
            book.get().setTitle(updateBook.getTitle());
            book.get().setDescription(updateBook.getDescription());
            book.get().setAuthorName(updateBook.getAuthorName());
            if(updateBook.getCategories().size() == 0)return bookRepository.save(book.get());
            if (updateBook.getCategories().isEmpty()) {
                categoryRepository.deleteBook_Category(bookId);
            } else {
                if (!book.get().getCategories().equals(updateBook.getCategories())) {
                    for ( Category c:
                         updateBook.getCategories()) {
                        if(c.getCategoryName() == null)return bookRepository.save(book.get());
                    }
                    categoryRepository.deleteBook_Category(bookId);
                    for (Category c : updateBook.getCategories()) {
                        categoryRepository.saveBook_Category(bookId, c.getCategoryId());
                    }
                }
            }
            return bookRepository.save(book.get());
        }
        return null;
    }

    //    @GetMapping("/{bookId}")
//    public Book getBookById(@PathVariable("bookId") Integer bookId) {
//        return bookRepository.findById(bookId).get();
//    }
//    @PostMapping("/update/{bookId}")
//    Book updateBookById(@PathVariable Integer bookId, @RequestBody Book updateBook) {
//        Optional<Book> book = bookRepository.findById(bookId);
//        book.get().setTitle(updateBook.getTitle());
//        book.get().setDescription(updateBook.getDescription());
//        book.get().setAuthorName(updateBook.getAuthorName());
//        if (updateBook.getCategories().isEmpty()) {
//            categoryRepository.deleteBook_Category(bookId);
//        } else {
//            if (!book.get().getCategories().equals(updateBook.getCategories())) {
//                categoryRepository.deleteBook_Category(bookId);
//                for (Category c : updateBook.getCategories()) {
//                    categoryRepository.saveBook_Category(bookId, c.getCategoryId());
//                }
//            }
//        }
//        return bookRepository.save(book.get());
//    }
    @PostMapping("/cover/update/{bookId}")
    public void updateCoverFile(@RequestParam("coverPath") MultipartFile file, @PathVariable("bookId") Integer bookId) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if ((fileExtension.equalsIgnoreCase("jpg")) && file.getSize() < 5000000) {
            String fileName = StringUtils.cleanPath(bookId + ".jpg");
            try {
                // Save the file to the uploads directory
                String uploadDir = System.getProperty("user.dir") + COVER_UPLOAD_DIR;
                file.transferTo(new File(uploadDir + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @PostMapping("/pdf/update/{bookId}")
    public void updatePdfFile(@RequestParam("pdfPath") MultipartFile file, @PathVariable("bookId") Integer bookId) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if ((fileExtension.equalsIgnoreCase("pdf")) && file.getSize() < 5000000) {
            String fileName = StringUtils.cleanPath(bookId + ".pdf");
            try {
                // Save the file to the uploads directory
                String uploadDir = System.getProperty("user.dir") + PDF_UPLOAD_DIR;
                file.transferTo(new File(uploadDir + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //    @PostMapping("/pdf/update/{bookId}")
//    public void updatePdfFile(@RequestParam("pdfPath") MultipartFile file, @PathVariable("bookId") Integer bookId) {
//        String fileExtension = getFileExtension(file.getOriginalFilename());
//        if ((fileExtension.equalsIgnoreCase("pdf")) && file.getSize() < 5000000) {
//            String fileName = StringUtils.cleanPath(bookId + ".pdf");
//            try {
//                // Save the file to the uploads directory
//                String uploadDir = System.getProperty("user.dir") + PDF_UPLOAD_DIR;
//                file.transferTo(new File(uploadDir + fileName));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
    @GetMapping("/search/{searchText}")
    ResponseEntity<Page<Book>> findAllPublic(
            @PathVariable String searchText,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (searchText.length() < 1) {
            return new ResponseEntity<>(bookRepository.findAll(pageable), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(bookRepository.findAllPublic(pageable, searchText), HttpStatus.OK);
        }
    }


    @GetMapping("/sort/{type}")
    ResponseEntity<Page<Book>> sortAllPublic(
            @PathVariable String field,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ResponseEntity<>(bookRepository.findAllPublic(pageable, field), HttpStatus.OK);
    }

    @GetMapping("/order")
    ResponseEntity<Page<Book>> orderbyAllPublic(
            @RequestParam(defaultValue = "bookId") String field,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ResponseEntity<>(bookRepository.orderAllPublic(pageable, field), HttpStatus.OK);
    }

    @GetMapping("/public/by-user")
    List<Book> getPublicBookByUser(HttpServletRequest request) {
        String token = jwtTokenFilter.getJwtFromRequest(request);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return bookRepository.getPublicBookByUsername(username);
    }

    @GetMapping("/unpublic/by-user")
    List<Book> getUnPublicBookByUser(HttpServletRequest request) {
        String token = jwtTokenFilter.getJwtFromRequest(request);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return bookRepository.getUnPublicBookByUsername(username);
    }

    @GetMapping("/mypublic/page")
    ResponseEntity<Page<Book>> findPageAllPublicByUser(
            HttpServletRequest request,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        String token = jwtTokenFilter.getJwtFromRequest(request);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ResponseEntity<>(bookRepository.getPublicBookByUsernamePage(pageable, username), HttpStatus.OK);
    }

    //đã sửa phuong
    @GetMapping("/myunpublic/page")
    ResponseEntity<Page<Book>> findPageAllUnPublicByUser(
            HttpServletRequest request,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize
    ) {
        String token = jwtTokenFilter.getJwtFromRequest(request);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new ResponseEntity<>(bookRepository.getUnPublicBookByUsernamePage(pageable, username), HttpStatus.OK);
    }


}
