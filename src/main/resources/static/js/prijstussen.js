"use strict";

import {byId, toon, verberg, verwijderChildElemandVan} from "./util.js";

byId("zoek").onclick = async function () {
    verbergPizzaEnFouten();
    const vanPrijs = byId("vanPrijs");
    if (!vanPrijs.checkValidity()) {
        toon("vanFout");
        vanPrijs.focus();
        return;
    }
    const totPrijs = byId("totPrijs");
    if (!totPrijs.checkValidity()) {
        toon("totFout");
        totPrijs.focus();
        return;
    }

    findPrijsTussen(vanPrijs.value, totPrijs.value);
};

function verbergPizzaEnFouten() {
    verberg("pizzasTable");
    verberg("vanFout");
    verberg("totFout");
    verberg("storing");
}

async function findPrijsTussen(vanPrijs, totPrijs) {
    const response = await fetch(`pizzas?vanPrijs=${vanPrijs}&totPrijs=${totPrijs}`);
    console.log(vanPrijs + " - " + totPrijs);
    if (response.ok) {
        const pizzas = await response.json();
        toon("pizzasTable");
        const pizzasBody = byId("pizzasBody");
        verwijderChildElemandVan(pizzasBody);
        console.log(pizzasBody);
        console.log(pizzas);
        for (const pizza of pizzas) {
            const tr = pizzasBody.insertRow();
            tr.insertCell().innerText = pizza.id;
            tr.insertCell().innerText = pizza.naam;
            tr.insertCell().innerText = pizza.prijs;
        }
    } else {
        toon("storing");
    }
}