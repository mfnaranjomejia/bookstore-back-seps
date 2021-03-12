package com.pluralsight.bookstore.repository;

import com.pluralsight.bookstore.model.Book;
import com.pluralsight.bookstore.util.NumberGenerator;
import com.pluralsight.bookstore.util.TextUtil;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */
@Transactional(SUPPORTS)
public class BookRepository {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @PersistenceContext(unitName = "bookStorePU")
    private EntityManager em;

    @Inject
    private NumberGenerator generator;

    @Inject
    private TextUtil textUtil;

    // ======================================
    // =          Business methods          =
    // ======================================

    public Book find(@NotNull Long id) {
        return em.find(Book.class, id);
    }

    public List<Book> findAll() {
        // Implementar la busqueda de todos los libros
        //SELECT * FROM Book ORDER BY title DESC
        CriteriaBuilder criteriaBuilder = this.em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root root = criteriaQuery.from(Book.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("title")));

        TypedQuery typedQuery = em.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    public Long countAll() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(b) FROM Book b", Long.class);
        return query.getSingleResult();
    }

    @Transactional(REQUIRED)
    public Book create(@NotNull Book book) {
        book.setIsbn(generator.generateNumber());
        book.setTitle(textUtil.sanitize(book.getTitle()));
        em.persist(book);
        return book;
    }

    @Transactional(REQUIRED)
    public void delete(@NotNull Long id) {
        em.remove(em.getReference(Book.class, id));
    }
}
