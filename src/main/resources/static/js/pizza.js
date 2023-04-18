"use strict";

import {byId, toon, verberg, setText} from "./util.js";

byId("zoek").onclick = async function () {
    verbergPizzaEnFouten();
    const zoekIdInput = byId("zoekId");
    if (zoekIdInput.checkValidity()) {
        findById(zoekIdInput.value);
    } else {
        toon("zoekIdFout");
        zoekIdInput.focus();
    }
}

byId("bewaar").onclick = async function () {
  const nieuwePrijsInput = byId("nieuwePrijs");
  if (!nieuwePrijsInput.checkValidity()) {
      toon("nieuwePrijsFout");
      return;
  }
  verberg("nieuwePrijsFout");
  updatePrijs(nieuwePrijsInput.value);
};

function verbergPizzaEnFouten() {
    verberg("pizza");
    verberg("zoekIdFout");
    verberg("nietGevonden");
    verberg("storing");
    verberg("nieuwePrijsFout");
}

async function findById(id) {
    const repsonse = await fetch(`pizzas/${id}`);
    if (repsonse.ok) {
        const pizza = await repsonse.json();
        toon("pizza");
        setText("naam", pizza.naam);
        setText("prijs", pizza.prijs);
    } else {
        if (repsonse.status === 404) {
            toon("nietGevonden");
        } else {
            toon("storing");
        }
    }
}

async function updatePrijs(nieuwePrijs) {
    const prijsWijziging = {
        prijs: nieuwePrijs
    };

    const response = await fetch(`pizzas/${byId("zoekId").value}/prijs`,
        {
            method: "PATCH",
            headers: {'Content-Type': "application/json"},
            body: JSON.stringify(prijsWijziging)
        });

    if (response.ok) {
        setText("prijs", nieuwePrijs)
    } else {
        toon("storing");
    }
}