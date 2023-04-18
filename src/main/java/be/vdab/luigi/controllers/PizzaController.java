package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.domain.PizzaPrijs;
import be.vdab.luigi.dto.NieuwePizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import be.vdab.luigi.services.PizzaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
public class PizzaController {

    private record IdNaamPrijs(long id, String naam, BigDecimal prijs) {
        IdNaamPrijs(Pizza pizza) {
            this(pizza.getId(), pizza.getNaam(), pizza.getPrijs());
        }

    }

    private record PrijsWijziging(@NotNull @PositiveOrZero BigDecimal prijs) {}

    private final PizzaService service;

    public PizzaController(PizzaService service) {
        this.service = service;
    }

    @GetMapping("pizzas/aantal")
    long findAantal() {
        return service.findAantal();
    }

    @GetMapping("pizzas/{id}")
    IdNaamPrijs findById(@PathVariable long id) {
        return service.findById(id)
                .map(pizza -> new IdNaamPrijs(pizza))
                .orElseThrow(() -> new PizzaNietGevondenException(id));
    }

    @GetMapping("pizzas")
    Stream<IdNaamPrijs> findAll() {
        return service.findAll()
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @GetMapping(value = "pizzas", params = "naamBevat")
    Stream<IdNaamPrijs> findByNaamBevat(String naamBevat) {
        return service.findByNaamBevat(naamBevat)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @GetMapping(value = "pizzas", params = {"vanPrijs", "totPrijs"})
    Stream<IdNaamPrijs> findByPrijsTussen(BigDecimal vanPrijs, BigDecimal totPrijs) {
        return service.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @DeleteMapping("pizzas/{id}")
    void delete(@PathVariable long id) {
        service.delete(id);
    }

    @PostMapping("pizzas")
    long create(@RequestBody @Valid NieuwePizza nieuwePizza) {
        var id = service.create(nieuwePizza);
        return id;
    }

    @PatchMapping("pizzas/{id}/prijs")
    void updatePrijs(@PathVariable long id, @RequestBody @Valid PrijsWijziging prijsWijziging) {
        var pizzaPrijs = new PizzaPrijs(prijsWijziging.prijs, id);
        service.updatePrijs(pizzaPrijs);
    }
}
