package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.PizzaPrijs;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PizzaPrijsRepository {

    private final JdbcTemplate template;

    public PizzaPrijsRepository(JdbcTemplate template) {
        this.template = template;
    }

    public void create(PizzaPrijs pizzaPrijs) {
        String statement = "insert into pizzaprijzen (prijs, vanaf, pizzaId) values (?,?,?)";
        template.update(statement, pizzaPrijs.getPrijs(), pizzaPrijs.getVanaf(), pizzaPrijs.getPizzaId());
    }
}
