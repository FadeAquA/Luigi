"use strict";

import {byId, toon, verwijderChildElemandVan, verberg} from "./util.js";

byId("zoek").onclick = async function () {
    verbergPizzaEnFouten();
    const woordInput = byId("woord");
    if (woordInput.checkValidity()) {
        await findByWoord(woordInput.value);
    } else {
        toon("woordFout");
        woordInput.focus();
    }
}

function verbergPizzaEnFouten() {
    verberg("pizzasTable");
    verberg("woordFout");
    verberg("storing");
}

async function findByWoord(woord) {
    const response = await fetch(`/pizzas?naamBevat=${woord}`);
    if (response.ok) {
        const pizzas = await response.json();
        toon("pizzasTable");
        const pizzaBody = byId("pizzasBody");
        verwijderChildElemandVan(pizzaBody);
        for (const pizza of pizzas) {
            const tr = pizzaBody.insertRow();
            tr.insertCell().innerText = pizza.id;
            tr.insertCell().innerText = pizza.naam;
            tr.insertCell().innerText = pizza.prijs;
        }
    } else {
        toon("storing");
    }
}