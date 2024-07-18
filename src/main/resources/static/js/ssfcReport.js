function main() {
    setSsfcYearChangeListener();
    setGroupingCheckboxListeners();
    setAddPeriodListener();
    setDefaultDeletePeriodListener();
    setChangeReportTypeListener();
    setDefaultSelectAllPeriodsListener();
}

function setDefaultSelectAllPeriodsListener() {
    let label = document.querySelector('.ssfc-period label:first-child');
    label.addEventListener("click", selectAllPeriodsListener);

    console.debug('Select/unselect all periods listener set');
}

function selectAllPeriodsListener(e) {
    let inputs = e
        .target.parentElement.getElementsByTagName('input');
    let status = !inputs[0].checked;

    for (let i = 0; i < inputs.length; i++) {
        inputs[i].checked = status;
    }

    console.debug('Select/unselect all periods clicked');
}

function setChangeReportTypeListener() {
    let allTypes = document.querySelectorAll('.select-type input[name=type]');
    allTypes.forEach(typeEl => typeEl
        .addEventListener('change', () => {
            renewSourceListListener();
            console.debug('Change report type listeners set');
        })
    );
}

function setSsfcYearChangeListener() {
    let yearElement = document.getElementById('year-ssfc');

    yearElement.addEventListener('change', renewSourceListListener);
}

function renewSourceListListener() {
    let years = getSsfcPeriodYears();
    let params = new URLSearchParams();
    years.forEach(year => params.append("years", year));

    try {
        fetch('/source/year?' + params,
            {method: 'GET'})
            .then(res => res.json())
            .then(res => renewSourceList(res));

        console.debug(`Source list requested for years ${years}`);
    } catch (err) {
        console.log(err.message);
    }
}

function renewSourceList(sources) {
    let noDataElement = document.querySelector(".no-data");
    let container = document.querySelector(".pick-sources");
    container.replaceChildren();

    if (sources.length === 0) {
        noDataElement.textContent = "НУР на указанный период не заведены в систему";
    } else {
        noDataElement.textContent = "";

        for (let i = 0; i < sources.length; i++) {
            let sourceElement = document.createElement("input");
            sourceElement.type = "checkbox";
            sourceElement.id = `source${i + 1}`;
            sourceElement.name = "sources";
            sourceElement.value = sources[i]['sourceId'];
            container.appendChild(sourceElement);

            sourceElement = document.createElement("label");
            sourceElement.textContent = sources[i]['sourceName'];
            sourceElement.setAttribute("for", `source${i + 1}`);
            container.appendChild(sourceElement);
        }
    }

    console.debug(`Ssfc report source list renewed (${sources.length} in total)`);
}

function getSsfcPeriodYears() {
    let years = [];

    if (document.querySelector(".ssfc-report form")
        .elements["type"].value === "period") {

        let periodsElement = document.querySelector(".ssfc-periods");
        let periodElements = periodsElement.getElementsByClassName("ssfc-period");

        for (const element of periodElements) {
            years.push(element.children.item(0).textContent.substring(0, 4));
        }
    } else {
        years.push(document.querySelector(".ssfc-report form").elements["year"].value);
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

function getBaseElement(year) {
    let periodsElement = document.querySelector(".ssfc-periods");
    let periodElements = periodsElement.getElementsByClassName("ssfc-period");

    for (const element of periodElements) {
        if (Number.parseInt(element.children.item(0).textContent.substring(0, 4)) > year) {

            console.debug(`Base period chosen ${element.children.item(0).textContent.substring(0, 4)}`);

            return element;
        }
    }

    console.debug(`Base period: last`);

    return null;
}

function setAddPeriodListener() {
    document.querySelector(".add-year-link").addEventListener("click", () => {
        let year = document.getElementById("year-ssfc").value;

        if (getSsfcPeriodYears().includes(year)) {
            console.log("Period already exists");

            showSsfcWarn("ПРЕДУПРЕЖДЕНИЕ: Период уже добавлен");
        } else {
            let base = getBaseElement(year);
            createPeriod(year, base);
            renewSourceListListener();
        }
    });

    console.debug(`Add period listener set`);
}

function setDefaultDeletePeriodListener() {
    document.querySelector(".delete-year-link")
        .addEventListener("click", deletePeriod);
}

function deletePeriod(e) {
    if (getSsfcPeriodYears().length === 1) {
        console.debug(`Period not deleted. At least one period has to be defined`);

        showSsfcWarn("ПРЕДУПРЕЖДЕНИЕ: Хотя бы один период должен быть выбран");
    } else {
        e.target.parentNode.remove();

        console.debug(`Period deleted`);

        renewSourceListListener();
    }
}

function showSsfcWarn(text) {
    let oldWarnElement = document.querySelector("#ssfc-warn-period");

    if (oldWarnElement !== null) {
        oldWarnElement.remove();

        console.debug("Old ssfc warn removed");
    }

    let base = document.querySelector(".select-type");
    let warn = document.createElement("span");
    warn.textContent = text;
    warn.style.color = "Maroon";
    warn.style.background = "LightSalmon";
    warn.id = "ssfc-warn-period";
    base.parentNode.insertBefore(warn, base);
    setTimeout(() => warn.remove(), 5000);
}

function createPeriod(year, base) {
    let months = ["Год", "Январь", "Февраль", "Март",
        "Апрель", "Май", "Июнь", "Июль", "Август",
        "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];

    let periods = document.querySelector(".ssfc-periods fieldset");
    let period = document.createElement("div");
    period.classList.add("ssfc-period");

    let label = document.createElement("label");
    label.textContent = `${year} год:`;
    period.appendChild(label);

    for (let i = 0; i < 13; i++) {
        let checkbox = document.createElement("input");
        checkbox.setAttribute("type", "checkbox");

        if (i !== 0) {
            checkbox.setAttribute("checked", "checked");
        }

        checkbox.setAttribute("name", "periods");
        checkbox.setAttribute("value", `${year}.${i}`);
        checkbox.id = `period-month-${year}-${i}`;

        let label = document.createElement("label");
        label.textContent = months[i];
        label.setAttribute("for", `period-month-${year}-${i}`);
        period.appendChild(document.createTextNode("\u00A0"));
        period.appendChild(checkbox);
        period.appendChild(document.createTextNode("\u00A0"));
        period.appendChild(label);
    }

    label = document.createElement("label");
    label.textContent = "Удалить";
    label.classList.add("delete-year-link");
    label.addEventListener("click", deletePeriod);

    period.appendChild(document.createTextNode("\u00A0"));
    period.appendChild(label);

    period.firstElementChild.addEventListener('click', selectAllPeriodsListener);

    periods.insertBefore(period, base);

    console.debug(`Period ${year} created`);
}

main();