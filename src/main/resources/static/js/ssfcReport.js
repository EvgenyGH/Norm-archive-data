function main() {
    setSsfcYearChangeListener();
    setGroupingCheckboxListeners();
    setAddPeriodListener();
    setDeletePeriodListener();
}

function setSsfcYearChangeListener() {
    let yearElement = document.getElementById('year-ssfc');

    yearElement.addEventListener('change', async event => {
        event.preventDefault();

        let years = getSsfcPeriodYears();
        let params = new URLSearchParams();
        years.forEach(year => params.append("years", year));

        try {
            fetch('/source/year?' + params,
                {method: 'GET'})
                .then(res => renewSourceList(res.json()));
            console.debug(`Source list requested for years ${years}`);
        } catch (err) {
            console.log(err.message);
        }
    });
}

function renewSourceList(sources) {
    sources.then(data => console.log(data));
}

function getSsfcPeriodYears() {
    let years = [];

    let periodsElement = document.querySelector(".ssfc-periods");
    let periodElements = periodsElement.getElementsByClassName("ssfc-period");

    for (const element of periodElements) {
        years.push(element.querySelector("#period-month-0").value.substring(0, 4));
    }

    console.debug(`Period years formed ${years}`);

    return years;
}


function setGroupingCheckboxListeners() {
    document.querySelector('input[value=branch]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=sumsOnly]').checked) {
            document.querySelector('input[value=tz]').checked = false;
            console.debug('tz unchecked');
        }
    });

    document.querySelector('input[value=tz]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=sumsOnly]').checked) {
            document.querySelector('input[value=branch]').checked = false;
            console.debug('branch unchecked');
        }
    });

    document.querySelector('input[value=sumsOnly]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=tz]').checked
            && document.querySelector('input[value=branch]').checked) {
            document.querySelector('input[value=branch]').checked = false;
            document.querySelector('input[value=tz]').checked = false;
            console.debug('branch and tz unchecked');
        }
    });
}

function setAddPeriodListener() {
    document.querySelector(".add-year-link").addEventListener("click", e => {
        let year = document.getElementById("year-ssfc").value;
        console.debug(`Add period listener set`);
        createPeriod(year);
    });
}

function setDeletePeriodListener() {
    document.querySelector(".delete-year-link").addEventListener("click", e => {
        let parent = e.target.parentNode.remove();
        console.debug(`Period deleted`);
    });
}

function createPeriod(year) {
    let months = ["Год", "Январь", "Февраль", "Март",
        "Апрель", "Май", "Июнь", "Июль", "Август",
        "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];

    let periods = document.querySelector(".ssfc-periods fieldset");
    let period = document.createElement("div");
    period.classList.add("ssfc-period");

    let label = document.createElement("label");
    label.textContent = `${year} год:`;
    period.appendChild(label);
    periods.appendChild(period);


    for (let i = 0; i < 13; i++) {
        let checkbox = document.createElement("input");
        checkbox.setAttribute("type", "checkbox");
        checkbox.setAttribute("checked", "checked");
        checkbox.setAttribute("name", "periods");
        checkbox.setAttribute("value", `${year}${i}`);
        checkbox.id = `period-month-${i}`;

        let label = document.createElement("label");
        label.textContent = months[i];
        label.setAttribute("for", `period-month-${i}`);
        period.appendChild(document.createTextNode("\u00A0"));
        period.appendChild(checkbox);
        period.appendChild(document.createTextNode("\u00A0"));
        period.appendChild(label);

        periods.appendChild(period);
    }

    label = document.createElement("label");
    label.textContent = "Удалить";
    label.classList.add("delete-year-link");
    label.addEventListener("click", e => {
        let parent = e.target.parentNode.remove();
        console.debug(`Period deleted`);
    });

    period.appendChild(document.createTextNode("\u00A0"));
    period.appendChild(label);

    console.debug(`Period ${year} created`);
}

main();