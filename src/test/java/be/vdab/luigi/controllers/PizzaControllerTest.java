package be.vdab.luigi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/pizzas.sql")
@AutoConfigureMockMvc
class PizzaControllerTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String PIZZAS = "pizzas";
    private static final String PIZZA_PRIJZEN = "pizzaprijzen";
    private final static Path TEST_RESOURCES = Path.of("src/test/resources");
    private final MockMvc mvc;

    PizzaControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    private long idVanTestPizza() {
        return jdbcTemplate.queryForObject("select id from pizzas where naam='test1'", Long.class);
    }

    @Test
    void findAantal() throws Exception {
        mvc.perform(get("/pizzas/aantal"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").value(countRowsInTable(PIZZAS)));
    }

    @Test
    void findById() throws Exception {
        var id = idVanTestPizza();
        mvc.perform(get("/pizzas/{id}", id))
                .andExpectAll(status().isOk(),
                        jsonPath("id").value(id),
                        jsonPath("naam").value("test1"));
    }

    @Test
    void findByIdGeeftNotFoundBijEenOnbestaandePizza() throws Exception {
        mvc.perform(get("/pizzas/{id}",  Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception {
        mvc.perform(get("/pizzas"))
                .andExpectAll(status().isOk(),
                        jsonPath("length()").value(countRowsInTable(PIZZAS)));
    }

    @Test
    void findByNaamBevat() throws Exception {
        mvc.perform(get("/pizzas")
                        .param("naamBevat", "test"))
                .andExpectAll(status().isOk(), jsonPath("length()").value(
                        countRowsInTableWhere(PIZZAS, "naam like '%test%'")));
    }

    @Test
    void findByPrijsTussen() throws Exception {
        mvc.perform(get("/pizzas")
                        .param("vanPrijs", "10")
                        .param("totPrijs", "20"))
                .andExpectAll(status().isOk(), jsonPath("length()").value(
                        countRowsInTableWhere(PIZZAS, "prijs between 10 and 20")));
    }

    @Test
    void deleteVerwijdertPizza() throws Exception {
        var id = idVanTestPizza();
        mvc.perform(delete("/pizzas/{id}", id))
                .andExpect(status().isOk());
        assertThat(countRowsInTableWhere(PIZZAS, "id=" + id)).isZero();
    }

    @Test
    void create() throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve("correctePizza.json"));
        var responseBody = mvc.perform(post("/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(countRowsInTableWhere(PIZZAS, "naam= 'test4' and id=" + responseBody)).isOne();
    }

    @ParameterizedTest
    @ValueSource(strings = {"pizzaZonderNaam.json", "pizzaMetLegeNaam.json", "pizzaZonderPrijs.json", "pizzaMetNegatievePrijs.json"})
    void createMetVerkeerdeDataMislukt(String bestandNaam) throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve(bestandNaam));
        mvc.perform(post("/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWijzigingPrijsEnVoegPizzaToe() throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve("correctePrijsWijziging.json"));
        mvc.perform(patch("/pizzas/{id}/prijs", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"prijsWijzigingZonderPrijs.json", "prijsWijzigingMetNegatievePrijs.json"})
    void patchMetVerkeerdePrijsMislukt(String bestandNaam) throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve(bestandNaam));
        var id = idVanTestPizza();
        mvc.perform(patch("/pizzas/{id}/prijs", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eenPizzaToevoegenDieAlBestaatMislukt() throws Exception {
        var jsonData = Files.readString(TEST_RESOURCES.resolve("pizzaDieAlBestaat.json"));
        mvc.perform(post("/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isConflict());
    }
}