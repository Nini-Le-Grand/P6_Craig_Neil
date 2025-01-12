function checkTransactionInput () {
    const inputs = Array.from(document.forms[0].elements).filter(input => input.type !== 'hidden');
    const submitButton = document.getElementById("transaction-btn");
    const areFieldsFilled = inputs.every(element => inputs.every(field => field.tagName !== "BUTTON" ? (field.value.trim() !== "") : true));

    submitButton.disabled = !areFieldsFilled;
}

function updateSelectStyling(selectElement) {
    if (selectElement.value === "") {
        selectElement.classList.add("default-selected");
    } else {
        selectElement.classList.remove("default-selected");
    }
}

function formatAmountValue(input) {
    if (input.value !== "0") {
        const number = parseFloat(input.value);
        input.value = number.toFixed(2);
    }
}

function checkEmptyValue(input) {
    if (input.value === "0") {
        input.value = null;
    }
}