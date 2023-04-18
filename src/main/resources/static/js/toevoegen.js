"use strict";

import {byId, setText, toon, verberg} from "./util.js";

byId("toevoegen").onclick = async function () {
    verbergFouten();
    const naamInput = byId("naam");
    if (!naamInput.checkValidity()) {
        toon("naamFout");
        naamInput.focus();
        return;
    }
    const prijsInput = byId("prijs");
    if (!prijsInput.checkValidity()) {
        toon("prijsFout");
        return;
    }
    const pizza = {
        naam: naamInput.value,
        prijs: prijsInput.value
    };
    voegToe(pizza);
}

async function voegToe(pizza) {
    const response = await fetch("pizzas", {method: "POST", headers: {'Content-Type': "application/json"},
    body: JSON.stringify(pizza)});

    if (response.ok) {
        window.location = "allepizzas.html";
    } else {
        if (response.status === 409) {
            const responseBody = await response.json();
            setText("conflict", responseBody.message);
            toon("conflict");
        } else {
            toon("storing");
        }
    }
}


function verbergFouten() {
    verberg("storing")
    verberg("naamFout")
    verberg("prijsFout")
    verberg("conflict")
}