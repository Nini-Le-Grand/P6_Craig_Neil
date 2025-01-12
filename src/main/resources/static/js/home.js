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

function toggleButton(input) {
    if(input.id === "creditAmount") {
        const submitButtonId = "credit-btn";
        const submitButton = document.getElementById(submitButtonId);
        const emptyField = input.value === "0" || input.value === null || input.value === "";
        submitButton.disabled = emptyField;
    }

    if(input.id === "withdrawAmount") {
        const submitButtonId = "withdraw-btn";
        const submitButton = document.getElementById(submitButtonId);
        const emptyField = input.value === "0" || input.value === null || input.value === "";
        submitButton.disabled = emptyField;
    }
}