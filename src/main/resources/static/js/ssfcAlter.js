function checkSsfcTableData() {
    let table = document.querySelector(".list-data");
    let warns = document.querySelector(".alter-ssfc .ssfc-warnings");
    let rows = table.getElementsByTagName("tr");
    let headers = rows[0].getElementsByTagName("th");
    let generations = rows[1].getElementsByTagName("input");
    let ownNeedss = rows[2].getElementsByTagName("input");
    let productions = rows[3].getElementsByTagName("input");
    let ssfcgs = rows[4].getElementsByTagName("input");
    let ssfcs = rows[5].getElementsByTagName("input");

    warns.replaceChildren();

    for (let i = 0; i < generations.length; i++) {
        let generation = generations[i].value;
        let production = productions[i].value;
        let ownNeeds = ownNeedss[i].value;
        let ssfcg = ssfcgs[i].value;
        let ssfc = ssfcs[i].value;
        let balance = Math.abs(generation - production - ownNeeds);

        if (balance > 0.000001) {
            let newNode = document.createElement("div");
            newNode.textContent = `ПРЕДУПРЕЖДЕНИЕ: ${headers[i + 2].textContent}. Небаланс ${balance.toFixed(6)}.`;
            warns.appendChild(newNode);
        }

        if (generation > 0 && ssfc < 142.86) {
            let newNode = document.createElement("div");
            newNode.textContent = `ПРЕДУПРЕЖДЕНИЕ: ${headers[i + 2].textContent}. УРУТ на отпуск т/э < 142.86 кг.у.т./Гкал.`;
            warns.appendChild(newNode);
        }

        if (generation > 0 && ssfcg < 142.86) {
            let newNode = document.createElement("div");
            newNode.textContent = `ПРЕДУПРЕЖДЕНИЕ: ${headers[i + 2].textContent}. УРУТ на выработку т/э < 142.86 кг.у.т./Гкал.`;
            warns.appendChild(newNode);
        }

        if (generation <= 0 && (production > 0 || ownNeeds > 0 || ssfcg > 0 || ssfc > 0)) {
            let newNode = document.createElement("div");
            newNode.textContent = `ПРЕДУПРЕЖДЕНИЕ: ${headers[i + 2].textContent}. При нулевой выработке т/э заведены ненулевые показатели.`;
            warns.appendChild(newNode);
        }
    }

    return headers[0].textContent;
}

function addFormChangeListener() {
    let table = document.querySelector(".list-data");
    let inputs = table.getElementsByTagName("input");

    for (let input of inputs) {
        input.addEventListener("change", checkSsfcTableData);
    }
}

checkSsfcTableData();

addFormChangeListener();