package be.vdab.luigi.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(PizzaRepository.class)
@Sql("/pizzas.sql")
class PizzaRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String PIZZAS = "pizzas";
    private final PizzaRepository repository;

    PizzaRepositoryTest(PizzaRepository repository) {
        this.repository = repository;
    }

    @Test
    void findAantal() {
        assertThat(repository.findAantal()).isEqualTo(countRowsInTable(PIZZAS));
    }

    @Test
    void findPizzasBetweenPrijs() {
        var van = BigDecimal.valueOf(4);
        var tot = BigDecimal.valueOf(5);
        assertThat(repository.findByPrijsTussen(van, tot))
                .hasSize(countRowsInTableWhere(PIZZAS, "prijs between " + van + " and " + tot))
                .allSatisfy(pizza -> assertThat(pizza.getPrijs()).isBetween(van, tot));
    }

}