function main() {
    document.querySelector('input[value=branch]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=sumsOnly]').checked) {
            document.querySelector('input[value=tz]').checked = false;
            console.log('tz unchecked');
        }
    });

    document.querySelector('input[value=tz]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=sumsOnly]').checked) {
            document.querySelector('input[value=branch]').checked = false;
            console.log('branch unchecked');
        }
    });

    document.querySelector('input[value=sumsOnly]').addEventListener('change', (e) => {
        if (e.target.checked && document.querySelector('input[value=tz]').checked
            && document.querySelector('input[value=branch]').checked) {
            document.querySelector('input[value=branch]').checked = false;
            document.querySelector('input[value=tz]').checked = false;
            console.log('branch and tz unchecked');
        }
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
    period.appendChild(document.createTextNode("\u00A0"));
    period.appendChild(label);

}

main();