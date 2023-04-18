package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class PizzaRepository {

    private final JdbcTemplate template;
    private final RowMapper<Pizza> pizzaMapper = (result, rowNum) ->
            new Pizza(result.getLong("id"), result.getString("naam"),
                    result.getBigDecimal("prijs"), result.getBigDecimal("winst"));

    public PizzaRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long findAantal() {
        String statement = "select count(*) as aantalPizzas from pizzas";
        return template.queryForObject(statement, Long.class);
    }

    public Optional<Pizza> findById(long id) {
        try {
            String statement = "SELECT * FROM pizzas where id=?";
            return Optional.of(template.queryForObject(statement, pizzaMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Pizza> findAll() {
        String statement = "select * from pizzas order by naam";
        return template.query(statement, pizzaMapper);
    }

    public List<Pizza> findByNaamBevat(String woord) {
        String statement = "select * from pizzas where naam like ? order by naam";
        return template.query(statement, pizzaMapper, "%" + woord + "%");
    }

    public List<Pizza> findByPrijsTussen(BigDecimal vanPrijs, BigDecimal totPrijs) {
        String statement = "select * from pizzas where prijs between ? and ? order by prijs";
        return template.query(statement, pizzaMapper, vanPrijs, totPrijs);
    }

    public void delete(long id) {
        String statement = "delete from pizzas where id = ?";
        template.update(statement, id);
    }

    public long create(Pizza pizza) {
        String statement = "insert into pizzas(naam, prijs, winst) values (?,?,?)";
        var keyholder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, pizza.getNaam());
            preparedStatement.setBigDecimal(2, pizza.getPrijs());
            preparedStatement.setBigDecimal(3, pizza.getWinst());
            return preparedStatement;
        }, keyholder);
        return keyholder.getKey().longValue();
    }

    public void updatePrijs(long id, BigDecimal prijs) {
        String statement = "update pizzas set prijs = ? where id= ?";
        if (template.update(statement, prijs, id) == 0) {
            throw new PizzaNietGevondenException(id);
        }
    }
}
